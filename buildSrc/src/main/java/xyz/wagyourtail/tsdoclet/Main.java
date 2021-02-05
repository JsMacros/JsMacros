package xyz.wagyourtail.tsdoclet;

import com.sun.javadoc.*;
import xyz.wagyourtail.tsdoclet.parsers.ClassParser;
import xyz.wagyourtail.tsdoclet.parsers.EventParser;
import xyz.wagyourtail.tsdoclet.parsers.LibraryParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class Main {
    public static File outDir;
    public static FileHandler outputTS;
    
    public static final List<LibraryParser> libs = new LinkedList<>();
    public static final Map<String, Set<ClassParser>> classes = new LinkedHashMap<>();
    public static final List<EventParser> events = new LinkedList<>();
    
    public static RootDoc root;

    public static boolean start (RootDoc root) {
        System.out.println("start");
        Main.root = root;
        outputTS = new FileHandler(new File(outDir, "JsMacros.d.ts"));
        //clear version folder
        try {
            deleteFolder(outDir);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (!outDir.exists() && !outDir.mkdirs()) {
            System.err.println("Failed to create version dir");
            return false;
        }
    
        for (ClassDoc clazz : root.classes()) {
            for (AnnotationDesc desc : clazz.annotations()) {
                if (desc.annotationType().name().equals("Library")) {
                    String value = null;
                    for (AnnotationDesc.ElementValuePair val : desc.elementValues()) {
                        if (val.element().name().equals("value")) {
                            value = (String) val.value().value();
                        }
                    }
                    genLib(clazz, value);
                    break;
                }
                if (desc.annotationType().name().equals("Event")) {
                    String name = null;
                    String oldName = null;
                    for (AnnotationDesc.ElementValuePair val : desc.elementValues()) {
                        if (val.element().name().equals("value")) {
                            name = (String) val.value().value();
                        } else {
                            oldName = (String)val.value().value();
                        }
                    }
                    genEvent(clazz, name, oldName);
                    break;
                }
            }
        }
    
        try {
            outputTS.append("declare const event: Events.BaseEvent;\n" +
                             "declare const file: Java.java.io.File\n\n" +
                             
                             "declare namespace Events {\n" +
                                "\texport interface BaseEvent extends Java.Object {\n" +
                                    "\t\tgetEventName(): string;\n" +
                                "\t}");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        for (EventParser event : events) {
            try {
                outputTS.append("\n\n" + event.genTypeScript());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        try {
            outputTS.append("\n}");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    
        for (LibraryParser lib : libs) {
            try {
                outputTS.append("\n\n" + lib.genTypeScript());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    
        ClassParser.TSPackage pkg = null;
        while (ClassParser.topLevelPackage.dirty) {
            pkg = ClassParser.topLevelPackage.genTypeScript();
        }
    
        try {
            outputTS.append("\n\ndeclare" + pkg.genTypeScript().substring(6));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    
        return true;
    }
    public static int optionLength(String var0) {
        if (var0.equals("-d")) return 2;
        else if (var0.equals("-v")) return 2;
        else return 2;
    }
    
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        for (String[] option : options) {
            if (option[0].equals("-d")) {
                if (outDir != null) {
                    reporter.printError("outdir set more than once");
                    return false;
                }
                outDir = new File(option[1]);
                if (outDir.exists()) {
                    if (!outDir.isDirectory()) {
                        reporter.printError("output is an existing file");
                        return false;
                    }
                } else {
                    return outDir.mkdirs();
                }
            }
        }
        return true;
    }
    
    public static void genLib(ClassDoc lib, String libName) {
        libs.add(new LibraryParser(lib, libName));
    }
    
    public static void genEvent(ClassDoc event, String name, String oldName) {
        events.add(new EventParser(event, name, oldName));
    }
    
    public static void deleteFolder(File folder) throws IOException {
        if (!folder.exists()) return;
        if (!folder.isDirectory()) if (!folder.delete()) throw new IOException("failed to delete " + folder.getAbsolutePath());
        else {
            File[] files = folder.listFiles();
            if (files != null) { //some JVMs return null for empty dirs
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteFolder(f);
                    } else {
                        if (!f.delete()) throw new IOException("failed to delete " + f.getAbsolutePath());
                    }
                }
            }
            if (!folder.delete()) throw new IOException("failed to delete " + folder.getAbsolutePath());
        }
    }
    
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
