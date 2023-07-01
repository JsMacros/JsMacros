package xyz.wagyourtail.doclet.webdoclet.options;

import jdk.javadoc.doclet.Doclet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Links implements Doclet.Option {
    public static Map<String, String> externalPackages = new HashMap<>();

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "link external javadoc";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return List.of("-link");
    }

    @Override
    public String getParameters() {
        return "<javadocurl: URL>";
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(arguments.get(0) + "package-list").openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                externalPackages.put(line, arguments.get(0) + "index.html?" + line.replaceAll("\\.", "/") + "/");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
