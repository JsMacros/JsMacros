package xyz.wagyourtail.webdoclet;

import com.sun.javadoc.*;
import xyz.wagyourtail.FileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unused")
public class Main {
    public static File outDir;
    /**
     * package -> base url of package,
     * use with {@code + "ClassName.html"} to get final url.
     */
     public static String version;
    public static Map<String, String> externalPackages = new HashMap<>();
    
    public static boolean start(RootDoc root) {
        try {
            //create package-list
            Set<String> packages = new LinkedHashSet<>();
            Set<ClassDoc> classes = new LinkedHashSet<>();
            for (ClassDoc clazz : root.specifiedClasses()) {
                packages.add(clazz.containingPackage().name());
                classes.add(clazz);
            }
            for (PackageDoc pkg : root.specifiedPackages()) {
                packages.add(pkg.name());
                classes.addAll(Arrays.asList(pkg.allClasses()));
            }
            StringBuilder pkglist = new StringBuilder();
            for (String pkg : packages) {
                if (!externalPackages.containsKey(pkg)) pkglist.append(pkg).append("\n");
            }
            new FileHandler(new File(outDir, "package-list")).write(pkglist.toString());
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int optionLength(String var0) {
        return 2;
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
            } else if (option[0].equals("-link")) {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(option[1] + "package-list").openStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        externalPackages.put(line, option[1] + "index.html?" + line.replaceAll("\\.", "/") + "/");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (option[0].equals("-v")) {
                if (version != null) {
                    reporter.printError("version set more than once");
                    return false;
                }
                version = option[1];
            }
        }
        return true;
    }
    
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
