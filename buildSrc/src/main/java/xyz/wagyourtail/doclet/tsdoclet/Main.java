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
            outputTS.append("""
                declare const event: Events.BaseEvent;
                declare const file: _javatypes.java.io.File;
                declare const context: _javatypes.xyz.wagyourtail.jsmacros.core.language.EventContainer<any>;

                declare namespace Events {
                    export interface BaseEvent extends _javatypes.java.lang.Object {
                        getEventName(): string;
                    }""");

            for (EventParser event : eventClasses) {
                outputTS.append(StringHelpers.tabIn("\n\n" + event.genTSInterface()));
            }

            outputTS.append("\n}");

            for (LibraryParser lib : libraryClasses) {
                outputTS.append("\n\n" + lib.genTSInterface());
            }

            outputTS.append("\n\ndeclare " + classes.genTSTree());
            outputTS.append("\n\ndeclare namespace Java {");
            for (ClassParser clz : classes.getAllClasses()) {
                outputTS.append("\n").append("    export function type(className: \"").append(clz.getType().getQualifiedName().toString()).append("\"): ")
                        .append("_javatypes.java.lang.Class<").append(clz.getType().getQualifiedName().toString())
                        .append("> & _javatypes.").append(clz.getType().getQualifiedName().toString()).append(".static");
            }
            outputTS.append("\n}");

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
