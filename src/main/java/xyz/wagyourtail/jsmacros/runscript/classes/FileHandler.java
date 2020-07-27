package xyz.wagyourtail.jsmacros.runscript.classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    private File f;
    
    public FileHandler(String path) {
        this(new File(path));
    }
    
    public FileHandler(File path) {
        f = path;
    }
    
    public FileHandler write(String s) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f, false));
        out.write(s);
        out.close();
        return this;
    }
    
    public String read() throws IOException {
        String ret = "";
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line = in.readLine();
        while(line != null) {
            ret += line + "\n";
            line = in.readLine();
        }
        in.close();
        return ret;
    }
    
    public FileHandler append(String s) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
        out.write(s);
        out.close();
        return this;
    }
    
    public String toString() {
        return String.format("FileHandler:{\"file\": \"%s\"}", f.getAbsolutePath());
    }
}
