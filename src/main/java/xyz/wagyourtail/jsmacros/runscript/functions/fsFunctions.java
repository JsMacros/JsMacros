package xyz.wagyourtail.jsmacros.runscript.functions;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import xyz.wagyourtail.jsmacros.jsMacros;

public class fsFunctions {
    public String[] list(String path) {
        return new File(jsMacros.config.macroFolder, path).list();
    }
    
    public boolean exists(String path) {
        return new File(jsMacros.config.macroFolder, path).exists();
    }
    
    public boolean isDir(String path) {
        return new File(jsMacros.config.macroFolder, path).isDirectory();
    }
    
    public String getName(String path) {
        return new File(jsMacros.config.macroFolder, path).getName();
    }
    
    public boolean makeDir(String path) {
        return new File(jsMacros.config.macroFolder, path).mkdir();
    }
    
    public void move(String from, String to) throws IOException {
        Files.move(new File(jsMacros.config.macroFolder, from), new File(jsMacros.config.macroFolder, to));
    }
    
    public void copy(String from, String to) throws IOException {
        Files.copy(new File(jsMacros.config.macroFolder, from), new File(jsMacros.config.macroFolder, to));
    }
    
    public boolean delete(String path) {
        return new File(jsMacros.config.macroFolder, path).delete();
    }
    
    public String combine(String patha, String pathb) throws IOException {
        String f = new File(new File(jsMacros.config.macroFolder, patha), pathb).getCanonicalPath().substring(jsMacros.config.macroFolder.getCanonicalPath().length());
        if (f.startsWith("/")) return "."+f;
        return f;
    }
    
    public String getDir(String path) throws IOException {
        String f = new File(jsMacros.config.macroFolder, path).getParentFile().getCanonicalPath().substring(jsMacros.config.macroFolder.getCanonicalPath().length());
        if (f.startsWith("/")) return "."+f;
        return f;
    }
}
