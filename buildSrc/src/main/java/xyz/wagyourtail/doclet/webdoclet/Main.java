package xyz.wagyourtail.doclet.webdoclet;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.gradle.plugins.ide.eclipse.model.Link;
import xyz.wagyourtail.FileHandler;
import xyz.wagyourtail.doclet.options.IgnoredItem;
import xyz.wagyourtail.doclet.options.OutputDirectory;
import xyz.wagyourtail.doclet.options.Version;
import xyz.wagyourtail.doclet.webdoclet.options.Links;
import xyz.wagyourtail.doclet.webdoclet.options.McVersion;
import xyz.wagyourtail.doclet.webdoclet.parsers.ClassParser;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Main implements Doclet {
    public static Reporter reporter;
    public static String mappingViewerURL;
    public static Elements elementUtils;
    public static DocTrees treeUtils;
    public static Map<Element, ClassParser> internalClasses = new LinkedHashMap<>();

    @Override
    public void init(Locale locale, Reporter reporter) {
        Main.reporter = reporter;
    }

    @Override
    public String getName() {
        return "WebDoc Generator";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Set.of(
            new Version(),
            new McVersion(),
            new OutputDirectory(),
            new Links(),
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
        mappingViewerURL = "https://wagyourtail.xyz/Projects/Minecraft%20Mappings%20Viewer/App?mapping=yarn,yarnIntermediary&version=" + McVersion.mcVersion + "&search=";
        Set<? extends Element> elements = environment.getIncludedElements();
        treeUtils = environment.getDocTrees();
        elementUtils = environment.getElementUtils();

        File outDir = new File(OutputDirectory.outputDir, Version.version);

        try {
            if (!outDir.exists() && !outDir.mkdirs()) {
                reporter.print(Diagnostic.Kind.ERROR, "Failed to create version dir\n");
                return false;
            }

            //create package-list
            StringBuilder pkgList = new StringBuilder();
            elements.stream().filter(e -> e.getKind() == ElementKind.PACKAGE).map(e -> (PackageElement) e).forEach(e -> {
                if (Links.externalPackages.containsKey(e.getQualifiedName().toString())) return;
                pkgList.append(e.getQualifiedName()).append("\n");
            });
            pkgList.setLength(pkgList.length() - 1);
            new FileHandler(new File(outDir, "package-list")).write(pkgList.toString());

            elements.stream().filter(e -> e instanceof TypeElement).map(e -> (TypeElement) e).forEach(e -> {
                AnnotationMirror mirror = e.getAnnotationMirrors().stream().filter(a -> a.getAnnotationType().asElement().getSimpleName().toString().equals("Event")).findFirst().orElse(null);
                //Event
                if (mirror != null) {
                    internalClasses.put(e, new ClassParser(e, "Event", getAnnotationValue("value", mirror).toString()));
                    return;
                }

                mirror = e.getAnnotationMirrors().stream().filter(a -> a.getAnnotationType().asElement().getSimpleName().toString().equals("Library")).findFirst().orElse(null);
                //Library
                if (mirror != null) {
                    internalClasses.put(e, new ClassParser(e, "Library", getAnnotationValue("value", mirror).toString()));
                    return;
                }

                internalClasses.put(e, new ClassParser(e, "Class", null));
            });

            StringBuilder searchList = new StringBuilder();
            for (ClassParser value : internalClasses.values()) {
                searchList.append(value.genSearchData());
                File out = new File(outDir, value.getPathPart() + ".html");
                File parent = out.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    reporter.print(Diagnostic.Kind.ERROR, "Failed to create package dir " + parent + "\n");
                    return false;
                }
                new FileHandler(out).write(value.genXML());
            }
            new FileHandler(new File(outDir, "search-list")).write(searchList.toString());



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
