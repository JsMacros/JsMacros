package xyz.wagyourtail.doclet.tsdoclet;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import xyz.wagyourtail.FileHandler;
import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.options.IgnoredItem;
import xyz.wagyourtail.doclet.options.OutputDirectory;
import xyz.wagyourtail.doclet.options.Version;
import xyz.wagyourtail.doclet.tsdoclet.parsers.ClassParser;
import xyz.wagyourtail.doclet.tsdoclet.parsers.EventParser;
import xyz.wagyourtail.doclet.tsdoclet.parsers.LibraryParser;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main implements Doclet {
    public static Reporter reporter;
    public static FileHandler outputTS;
    public static PackageTree classes = new PackageTree("_javatypes");
    public static DocTrees treeUtils;
    public static Set<String> redirectNeeded = new HashSet<>();

    @Override
    public void init(Locale locale, Reporter reporter) {
        Main.reporter = reporter;
    }

    @Override
    public String getName() {
        return "TypeScript Generator";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Set.of(
            new Version(),
            new OutputDirectory(),
            new IgnoredItem("-doctitle", 1),
            new IgnoredItem("-notimestamp", 0),
            new IgnoredItem("-windowtitle", 1)
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_16;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        Set<? extends Element> elements = environment.getIncludedElements();
        treeUtils = environment.getDocTrees();

        Set<LibraryParser> libraryClasses = new LinkedHashSet<>();
        Set<EventParser> eventClasses = new LinkedHashSet<>();


        outputTS = new FileHandler(new File(OutputDirectory.outputDir, "JsMacros-" + Version.version + ".d.ts"));

        if (!OutputDirectory.outputDir.exists() && !OutputDirectory.outputDir.mkdirs()) {
            reporter.print(Diagnostic.Kind.ERROR, "Failed to create version dir\n");
            return false;
        }

        if (outputTS.getFile().exists() && !outputTS.getFile().delete()) {
            reporter.print(Diagnostic.Kind.ERROR, "Failed to remove old ts output\n");
            return false;
        }

        elements.stream().filter(e -> e instanceof TypeElement).map(e -> (TypeElement) e).forEach(e -> {
            for (AnnotationMirror annotationMirror : e.getAnnotationMirrors()) {
                String annotationName = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
                if (annotationName.equals("Library")) {
                    libraryClasses.add(new LibraryParser(e, getAnnotationValue("value", annotationMirror).toString()));
                }
                if (annotationName.equals("Event")) {
                    eventClasses.add(new EventParser(e, getAnnotationValue("value", annotationMirror).toString()));
                }
            }
            if (e.getSimpleName().toString().equals("EventContainer")) {
                classes.addClass(e);
                System.out.println(e);
            }
        });

        try {
            
            outputTS.append(
                """
                declare const event: Events[keyof Events];
                declare const file: _javatypes.java.io.File;
                declare const context: EventContainer;

                declare namespace Events {

                    export interface BaseEvent extends JavaObject {
                        getEventName(): string;
                    }"""
            );
            for (EventParser event : eventClasses) {
                outputTS.append("\n\n" + StringHelpers.tabIn(event.genTSInterface()));
            }

            outputTS.append("\n\n}\n\ninterface Events {\n");
            for (EventParser event : eventClasses) {
                outputTS.append("\n    ").append(event.getName())
                    .append(": Events.").append(event.getName()).append(";");
            }
            outputTS.append("\n\n}");

            for (LibraryParser lib : libraryClasses) {
                outputTS.append("\n\n" + lib.genTSInterface());
            }

            outputTS.append("\n\ndeclare " + classes.genTSTree());
            outputTS.append("\n\ninterface JavaTypeDict {");
            List<ClassParser> allClasses = classes.getAllClasses();
            for (ClassParser clz : allClasses) {
                outputTS.append("\n    \"").append(clz.getTypeString().replaceFirst("<.+$", "").replace("_javatypes.", "")).append("\": ")
                    .append("JavaClass<").append(clz.getShortifiedType().replaceFirst("<.+$", "").replaceFirst("^\\$", "").replace(".function.", "._function."))
                    .append("> & ").append(clz.getTypeString().replaceFirst("<.+$", "").replace(".function.", "._function.")).append(".static;");
            }
            outputTS.append("\n}\n\ntype _ = { [none: symbol]: never }; // to trick vscode to rename types\n");

            int maxLen = 0;
            int maxRedirLen = 0;
            for (ClassParser clz : allClasses) { // check length
                String type = clz.getTypeString();
                if (!type.startsWith("_javatypes.xyz.")) continue;
                String shortified = clz.getClassName(true).replaceAll("([A-Z])(?=[,>])", "$1 = any");
                if (redirectNeeded.contains(shortified.replaceFirst("<.+$", ""))) {
                    if (shortified.length() > maxRedirLen) maxRedirLen = shortified.length();
                }
                if (shortified.length() > maxLen) maxLen = shortified.length();
            }

            for (ClassParser clz : allClasses) { // append shortify
                String type = clz.getTypeString();
                if (!type.startsWith("_javatypes.xyz.")) continue;
                String shortified = clz.getClassName(true).replaceAll("([A-Z])(?=[,>])", "$1 = any");
                
                outputTS.append("\ntype ").append(String.format("%-" + maxLen + "s", shortified))
                    .append(" = ").append(type.endsWith(">") ? "  " : "_&").append(type).append(";");
            }

            // since some type will refer to the long name inside same namespace
            outputTS.append("\n\ntype _r = { [none: symbol]: never };\n// redirects");
            for (ClassParser clz : allClasses) { // append redirects
                String shortified = clz.getClassName(true);
                if (!redirectNeeded.contains(shortified.replaceFirst("<.+$", ""))) continue;

                outputTS.append("\ntype $")
                    .append(String.format("%-" + maxRedirLen + "s", shortified.replaceAll("([A-Z])(?=[,>])", "$1 = any")))
                    .append(" = ").append(shortified.endsWith(">") ? "   " : "_r&")
                    .append(shortified).append(";");
            }

            outputTS.append("\n");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Object getAnnotationValue(String key, AnnotationMirror annotation) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> el : annotation.getElementValues().entrySet()) {
            if (el.getKey().getSimpleName().toString().equals(key))
                return el.getValue().getValue();
        }
        return null;
    }

}
