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
    
    public void write(String s) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f, false));
        out.write(s);
        out.close();
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
    
    public void append(String s) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
        out.write(s);
        out.close();
    }
}
