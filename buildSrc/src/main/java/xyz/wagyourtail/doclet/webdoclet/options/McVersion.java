package xyz.wagyourtail.doclet.webdoclet.options;

import jdk.javadoc.doclet.Doclet;

import java.util.List;

public class McVersion implements Doclet.Option {
    public static String mcVersion;

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Minecraft Version Number";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return List.of("-mcv", "--mcversion");
    }

    @Override
    public String getParameters() {
        return "<McVersion: SemVer>";
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        mcVersion = arguments.get(0);
        return true;
    }

}
