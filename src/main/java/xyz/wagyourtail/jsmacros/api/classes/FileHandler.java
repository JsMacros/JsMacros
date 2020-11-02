package xyz.wagyourtail.jsmacros.api.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Wagyourtail
 *
 * @since 1.1.8
 *
 */
public class FileHandler {
    private final File f;
    
    public FileHandler(String path) {
        this(new File(path));
    }
    
    public FileHandler(File path) {
        f = path;
    }
    
    /**
     * @since 1.1.8
     * 
     * @param s
     * @return
     * @throws IOException
     */
    public FileHandler write(String s) throws IOException {
        FileWriter out = new FileWriter(f, false);
        out.write(s);
        out.close();
        return this;
    }
    
    /**
     * @since 1.1.8
     * 
     * @param b
     * @return
     * @throws IOException
     */
    public FileHandler write(byte[] b) throws IOException {
        FileOutputStream out = new FileOutputStream(f,false);
        out.write(b);
        out.close();
        return this;
    }
    
    /**
     * @since 1.1.8
     * 
     * @return
     * @throws IOException
     */
    public String read() throws IOException {
        String ret = "";
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line = in.readLine();
            while (line != null) {
                ret += line + "\n";
                line = in.readLine();
            }
        }
        return ret;
    }
    
    /**
     * @since 1.2.6
     * 
     * @return
     * @throws IOException
     */
    public byte[] readBytes() throws IOException {
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] bytes =  new byte[(int) f.length()];
            in.read(bytes);
            return bytes;
        } catch (IOException e) {
            throw e;
        }
    }
    
    /**
     * @since 1.1.8
     * 
     * @param s
     * @return
     * @throws IOException
     */
    public FileHandler append(String s) throws IOException {
        FileWriter out = new FileWriter(f, true);
        out.write(s);
        out.close();
        return this;
    }
    
    /**
     * @since 1.2.6
     * 
     * @param b
     * @return
     * @throws IOException
     */
    public FileHandler append(byte[] b) throws IOException {
        FileOutputStream out = new FileOutputStream(f,true);
        out.write(b);
        out.close();
        return this;
    }
    
    public File getFile() {
        return f;
    }
    
    public String toString() {
        return String.format("FileHandler:{\"file\": \"%s\"}", f.getAbsolutePath());
    }
}
