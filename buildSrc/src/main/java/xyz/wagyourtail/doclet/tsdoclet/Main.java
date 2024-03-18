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
import xyz.wagyourtail.doclet.tsdoclet.parsers.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static xyz.wagyourtail.doclet.tsdoclet.parsers.AbstractParser.shouldIgnore;
import static xyz.wagyourtail.doclet.tsdoclet.parsers.ClassParser.mixinInterfaceMap;

public class Main implements Doclet {
    public static Reporter reporter;
    public static FileHandler outputTS;
    public static final PackageTree classes = new PackageTree("Packages");
    public static DocTrees treeUtils;
    public static Elements elementUtils;
    public static final Map<String, String> enumTypes = new TreeMap<>();

    public static final List<String> includedClassPath = List.of(
            "xyz.wagyourtail.jsmacros.client.api.helpers.",
            "xyz.wagyourtail.jsmacros.client.api.classes.inventory."
    );

    @Override
    public void init(Locale locale, Reporter reporter) {
        Main.reporter = reporter;
    }

    @Override
    public String getName() {
        return "TypeScript Generator";
    }

    @SuppressWarnings("SpellCheckingInspection")
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

        AbstractParser.initObjectElement();

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

        for (Element v : elements) if (v instanceof TypeElement e) {
            for (AnnotationMirror annotationMirror : e.getAnnotationMirrors()) {
                String annotationName = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
                switch (annotationName) {
                    case "Library" ->
                        libraryClasses.add(new LibraryParser(e, getAnnotationValue(annotationMirror).toString()));
                    case "Event" -> {
                        Boolean cancellableValue = (Boolean) getAnnotationValue(annotationMirror, "cancellable");
                        boolean cancellable = Boolean.TRUE.equals(cancellableValue);
                        eventClasses.add(new EventParser(e, getAnnotationValue(annotationMirror).toString(), cancellable));
                    }
                    case "Mixin" -> {
                        List<TypeElement> interfaces = e.getInterfaces().stream()
                                .filter(t -> t.getKind() == TypeKind.DECLARED)
                                .map(t -> (TypeElement) ((DeclaredType) t).asElement())
                                .filter(i -> !shouldIgnore(i))
                                .toList();
                        if (interfaces.isEmpty()) continue;

                        @SuppressWarnings("unchecked")
                        List<AnnotationValue> targets = (List<AnnotationValue>) getAnnotationValue(annotationMirror);
                        if (targets == null || targets.isEmpty()) continue;

                        for (AnnotationValue target : targets) {
                            TypeMirror type = (TypeMirror) target.getValue();
                            if (type.getKind() != TypeKind.DECLARED) continue;

                            TypeElement el = (TypeElement) ((DeclaredType) type).asElement();
                            mixinInterfaceMap.computeIfAbsent(el, k -> new HashSet<>()).addAll(interfaces);
                        }
                    }
                }
            }
            String qualifiedName = e.getQualifiedName().toString();
            if (includedClassPath.stream().anyMatch(qualifiedName::startsWith)) {
                classes.addClass(e);
            }
            if (e.getSimpleName().contentEquals("EventContainer")) {
                classes.addClass(e);
                System.out.println(e);
            }
        }

        try {
            // `\n\` to prevent java compiler from trimming the string
            outputTS.append(
                """
                \n\
                /**
                 * The global context  \n\
                 * If you're trying to access the context in {@link JsMacros.on},  \n\
                 * use the second param of callback
                 */
                declare const context: EventContainer;
                /**
                 * Assert and convert event type:
                 * ```js
                 * JsMacros.assertEvent(event, 'Service')
                 * ```
                 * If the type doesn't convert, that means the event type doesn't have any properties
                 */
                declare const event: Events.BaseEvent;
                declare const file: Packages.java.io.File;

                declare namespace Events {

                    interface BaseEvent extends JavaObject {

                        getEventName(): string;

                    }

                    interface Cancellable {

                        cancel(): void;

                    }"""
            );
            for (EventParser event : eventClasses) {
                outputTS.append("\n\n" + StringHelpers.tabIn(event.genTSInterface()));
            }

            // for type-safe event listener
            outputTS.append("\n\n}\n\ninterface Events {\n");
            for (EventParser event : eventClasses) {
                outputTS.append("\n    ").append(event.getName())
                    .append(": Events.").append(event.getName()).append(";");
            }
            outputTS.append("\n\n}");

            for (LibraryParser lib : libraryClasses) {
                outputTS.append("\n\n").append(lib.genTSInterface());
            }

            outputTS.append("\n\ndeclare ").append(classes.genTSTree()).append("\n");

            // short alias of jsmacros types, for jsdoc / type casting / type annotation and more
            // also used by some DocletReplace annotations
            Set<String> duplicateCheck = new HashSet<>();
            Set<String> sorter = new TreeSet<>();
            for (ClassParser clz : classes.getWagClasses()) {
                if (!duplicateCheck.add(clz.getClassName(false))) continue;
                clz.isPackage = false; // to trick it transfer full type
                sorter.add("\ntype " + clz.getClassName(true, true) + " = " +
                    clz.getQualifiedType() + ";");
                clz.isPackage = true;
            }
            outputTS.append(String.join("", sorter));

            // append number enums here because they are very unlikely to change
            //noinspection SpellCheckingInspection
            outputTS.append(
                """
                \n
                // Enum types
                type Bit    = 1 | 0;
                type Trit   = 2 | Bit;
                type Dit    = 3 | Trit;
                type Pentit = 4 | Dit;
                type Hexit  = 5 | Pentit;
                type Septit = 6 | Hexit;
                type Octit  = 7 | Septit;

                type Side = Hexit;
                type HotbarSlot = Octit | 8;
                type HotbarSwapSlot = HotbarSlot | OffhandSlot;
                type ClickSlotButton = HotbarSwapSlot | 9 | 10;
                type OffhandSlot = 40;

                """
            );

            for (Map.Entry<String, String> ent : enumTypes.entrySet()) {
                outputTS.append("type ").append(ent.getKey()).append(" = ").append(ent.getValue());
                if (!ent.getValue().contains("\n")) outputTS.append(";\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Object getAnnotationValue(AnnotationMirror annotation) {
        return getAnnotationValue(annotation, "value");
    }

    public static Object getAnnotationValue(AnnotationMirror annotation, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> el : annotation.getElementValues().entrySet()) {
            if (el.getKey().getSimpleName().contentEquals(key)) {
                return el.getValue().getValue();
            }
        }
        return null;
    }

}
