package xyz.wagyourtail.doclet.pydoclet;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import xyz.wagyourtail.FileHandler;
import xyz.wagyourtail.doclet.options.IgnoredItem;
import xyz.wagyourtail.doclet.options.OutputDirectory;
import xyz.wagyourtail.doclet.options.Version;
import xyz.wagyourtail.doclet.pydoclet.parsers.ClassParser;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Main implements Doclet {
    public static Reporter reporter;
    public static DocTrees treeUtils;
    public static Elements elementUtils;
    public static Types typeUtils;
    public static Set<? extends Element> elements;
    public static Map<Element, ClassParser> internalClasses;


    @Override
    public void init(Locale locale, Reporter reporter) {
        Main.reporter = reporter;
    }

    @Override
    public String getName() {
        return "Python generator";
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
        typeUtils = environment.getTypeUtils();
        treeUtils = environment.getDocTrees();
        elements = environment.getIncludedElements();
        elementUtils = environment.getElementUtils();
        internalClasses = new HashMap<>();
        HashMap<String, HashMap<String, String>> classes = new HashMap<>();

        //reporter.print(Diagnostic.Kind.NOTE, elements + "");

        File outDir = new File(OutputDirectory.outputDir, "");

        try {
            //Remove Folder
            if (outDir.exists() && !outDir.delete()) {
                reporter.print(Diagnostic.Kind.ERROR, "Failed to remove old py output\n");
                return false;
            }

            //Create Out Folder
            if (!outDir.exists() && !outDir.mkdirs()) {
                reporter.print(Diagnostic.Kind.ERROR, "Failed to create version dir\n");
                return false;
            }

            //Create ClassParser
            elements.stream().filter(e -> e instanceof TypeElement).map(e -> (TypeElement) e).forEach(e -> {
                internalClasses.put(e, new ClassParser(e));

                AnnotationMirror mirror = e.getAnnotationMirrors().stream().filter(a -> a.getAnnotationType().asElement().getSimpleName().toString().equals("Library")).findFirst().orElse(null);
                //Library
                if (mirror != null) {
                    //Library
                    if(!classes.containsKey("libraries")){
                        classes.put("libraries", new HashMap<>());
                    }
                    classes.get("libraries").put(ClassParser.getClassName(e), getAnnotationValue("value", mirror) + "");
                }else if(e.getAnnotationMirrors().stream().filter(a -> a.getAnnotationType().asElement().getSimpleName().toString().equals("Event")).findFirst().orElse(null) != null){
                    //Event
                    if(!classes.containsKey("events")){
                        classes.put("events", new HashMap<>());
                    }
                    classes.get("events").put(ClassParser.getClassName(e), getAnnotationValue("value", e.getAnnotationMirrors().stream().filter(a -> a.getAnnotationType().asElement().getSimpleName().toString().equals("Event")).findFirst().orElse(null)) + "");
                }else if(ClassParser.getClassName(e).contains("Helper")){
                    if(!classes.containsKey("helpers")){
                        classes.put("helpers", new HashMap<>());
                    }
                    classes.get("helpers").put(ClassParser.getClassName(e), ClassParser.getClassName(e));
                }else if(ClassParser.getClassName(e).contains("Mixin")){
                    if(!classes.containsKey("mixins")){
                        classes.put("mixins", new HashMap<>());
                    }
                    classes.get("mixins").put(ClassParser.getClassName(e), ClassParser.getClassName(e));
                }else{
                    if(!classes.containsKey("rest")){
                        classes.put("rest", new HashMap<>());
                    }
                    classes.get("rest").put(ClassParser.getClassName(e), ClassParser.getClassName(e));
                }

            });

            //Create Sum Up Files
            reporter.print(Diagnostic.Kind.NOTE, classes + "");
            for(Map.Entry<String, HashMap<String, String>> entry : classes.entrySet()){
                StringBuilder sb = new StringBuilder();

                sb.append("from typing import TypeVar\n\n");
                sb.append("from .EventContainer import EventContainer\n");
                sb.append("from .BaseEvent import BaseEvent\n");

                for(Map.Entry<String, String> args : entry.getValue().entrySet()) {
                    sb.append("from .").append(args.getKey()).append(" import ").append(args.getKey()).append("\n");
                }

                sb.append("\nFile = TypeVar[\"java.io.File\"]\n\n");

                if(entry.getKey().equalsIgnoreCase("libraries")){
                    sb.append("\n\n");
                    for(Map.Entry<String, String> args : entry.getValue().entrySet()) {
                        sb.append(args.getValue()).append(" = ").append(args.getKey()).append("()\n");
                    }

                    sb.append("context = EventContainer()\n");
                    sb.append("file = File()\n");
                    sb.append("event = BaseEvent()\n");
                }
                new FileHandler(new File(outDir, entry.getKey() + ".py")).write(sb.toString());
            }


            //Create Classes
            for(ClassParser value : internalClasses.values()){
                File out = new File(outDir, ClassParser.getClassName(value.type) + ".py");
                File parent = out.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    reporter.print(Diagnostic.Kind.ERROR, "Failed to create package dir " + parent + "\n");
                    return false;
                }
                new FileHandler(out).write(value.parseClass());
            }

            //Create __init__.py
            StringBuilder sb = new StringBuilder();
            for(String file : classes.keySet()){
                sb.append("from .").append(file).append(" import *\n");
            }
            new FileHandler(new File(outDir, "__init__.py")).write(sb.toString());


            sb.delete(0, sb.length());
            sb.append("""
                    from setuptools import setup, find_packages
                    from os import path
                    import os
                    import time
                                        
                    this_directory = path.abspath(path.dirname(__file__))
                    with open(path.join(this_directory, 'README.md'), encoding='utf-8') as f:
                        long_description = f.read()
                                        
                                        
                    VERSION = '""");
            sb.append(Version.version);
            sb.append("""
                    '
                    if "-" in VERSION: VERSION = VERSION.split("-")[0]
                    VERSION += "." + str(time.time()).split(".")[0][3:]
                    DESCRIPTION = 'A package to let your IDE know what JsMacros can do'
                    
                    def package_files(directory):
                        paths = []
                        for (path, directories, filenames) in os.walk(directory):
                            for filename in filenames:
                                paths.append(os.path.join('..', path, filename))
                        return paths
                        
                    extra_files = package_files('JsMacrosAC')
                                        
                    # Setting up
                    setup(
                        name="JsMacrosAC",
                        version=VERSION,
                        author="Hasenzahn1",
                        author_email="<motzer10@gmx.de>",
                        description=DESCRIPTION,
                        long_description_content_type="text/markdown",
                        long_description=long_description,
                        packages=["JsMacrosAC"],
                        package_data = {"": extra_files},
                        install_requires=[],
                        keywords=['python', 'JsMacros', 'Autocomplete', 'Doc'],
                        classifiers=[
                            "Intended Audience :: Developers",
                            "Programming Language :: Python :: 3",
                            "Operating System :: Unix",
                            "Operating System :: MacOS :: MacOS X",
                            "Operating System :: Microsoft :: Windows",
                        ]
                    )""");  
            new FileHandler(new File(outDir.getParent(), "setup.py")).write(sb.toString());

        }catch(Exception e){
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
