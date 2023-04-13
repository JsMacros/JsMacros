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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main implements Doclet {
    public static Reporter reporter;
    public static FileHandler outputTS;
    public static PackageTree classes = new PackageTree("Packages");
    public static DocTrees treeUtils;
    public static Elements elementUtils;
    public static Set<String> redirectNeeded = new HashSet<>();
    public static Map<String, String> enumTypes = new HashMap<>();

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
        elementUtils = environment.getElementUtils();

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
            
            outputTS.append("\n").append(
                """
                declare const event: Events.BaseEvent;
                declare const file: Packages.java.io.File;
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
                String comment = lib.genComment(lib.getType());
                if (comment.length() > 9) { // 9 because of the `function ` added in LibraryParser
                    outputTS.append("\n").append(
                        comment.substring(0, comment.length() - 9)
                            .replaceAll("\n \\*  An instance of this class is passed to scripts as the `\\w+` variable\\.", "")
                    );
                } else {
                    outputTS.append("\n\n");
                }
                outputTS.append("declare ").append(lib.genTSInterface());
            }

            outputTS.append("\n\ndeclare ").append(classes.genTSTree()).append("\n");

            int maxLen = 0;
            int maxRedirLen = 0;
            List<ClassParser> xyzClasses = classes.getXyzClasses();
            for (ClassParser clz : xyzClasses) { // check length
                String type = clz.getTypeString();
                String shortified = clz.getClassName(true).replaceAll("([A-Z])(?=[,>])", "$1 = any");
                if (redirectNeeded.contains(shortified.split("<", 2)[0])) {
                    if (shortified.length() > maxRedirLen) maxRedirLen = shortified.length();
                }
                if (shortified.length() > maxLen) maxLen = shortified.length();
            }

            for (ClassParser clz : xyzClasses) { // append shortify
                String type = clz.getTypeString();
                String shortified = clz.getClassName(true).replaceAll("([A-Z])(?=[,>])", "$1 = any");
                
                outputTS.append("\ntype ").append(String.format("%-" + maxLen + "s", shortified))
                    .append(" = ").append(type.endsWith(">") ? "    " : "_ & ").append(type).append(";");
            }

            // since some type will refer to the long name inside same namespace
            outputTS.append("\n\n// redirects\n");
            for (ClassParser clz : xyzClasses) { // append redirects
                String shortified = clz.getClassName(true);
                if (!redirectNeeded.contains(shortified.split("<", 2)[0])) continue;

                outputTS.append("type $")
                    .append(String.format("%-" + maxRedirLen + "s", shortified.replaceAll("([A-Z])(?=[,>])", "$1 = any")))
                    .append(" = ").append(shortified.endsWith(">") ? "     " : "_r & ")
                    .append(shortified).append(";\n");
            }

            // append number enums here because they are very unlikely to change
            outputTS.append("\n// Enum types\n").append(
                """
                type Bit    = 1 | 0
                type Trit   = 2 | Bit
                type Dit    = 3 | Trit
                type Pentit = 4 | Dit
                type Hexit  = 5 | Pentit
                type Septit = 6 | Hexit
                type Octit  = 7 | Septit

                type Side = Hexit
                type HotbarSlot = Octit | 8
                type HotbarSwapSlot = HotbarSlot | OffhandSlot
                type ClickSlotButton = HotbarSwapSlot | 9 | 10
                type OffhandSlot = 40

                """
            );

            for (String key : new TreeSet<>(enumTypes.keySet())) {
                outputTS.append("type ").append(key).append(" = ")
                    .append(enumTypes.get(key));
                if (!enumTypes.get(key).contains("\n")) outputTS.append(";\n");
            }

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
