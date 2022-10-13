package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @author Wagyourtail
 *
 * @since 1.1.8
 *
 */
@SuppressWarnings("unused")
public class FileHandler {
    private final File f;
    private final Charset charset;
    
    public FileHandler(String path) {
        this(new File(path), StandardCharsets.UTF_8);
    }

    public FileHandler(String path, String charset) {
        this(new File(path), charset);
    }

    public FileHandler(File path, String charset) {
        this(path, Objects.requireNonNull(Charset.forName(charset), () -> "Charset " + charset + " not found."));
    }

    public FileHandler(String path, Charset charset) {
        this(new File(path), charset);
    }

    public FileHandler(File path) {
        this(path, StandardCharsets.UTF_8);
    }

    public FileHandler(File path, Charset charset) {
        this.f = path;
        this.charset = charset;
    }
    
    /**
     *
     * writes a string to the file. this is a destructive operation that replaces the file contents.
     * @since 1.1.8
     * 
     * @param s
     * @return
     * @throws IOException
     */
    public FileHandler write(String s) throws IOException {
        try (OutputStreamWriter out = new OutputStreamWriter(Files.newOutputStream(f.toPath()), charset)) {
            out.write(s);
        }
        return this;
    }
    
    /**
     * writes a byte array to the file. this is a destructive operation that replaces the file contents.
     * @since 1.1.8
     * 
     * @param b
     * @return
     * @throws IOException
     */
    public FileHandler write(byte[] b) throws IOException {
        try (FileOutputStream out = new FileOutputStream(f, false)) {
            out.write(b);
        }
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
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(f.toPath()), charset))) {
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
        try (OutputStreamWriter out = new OutputStreamWriter(Files.newOutputStream(f.toPath()), charset)) {
            out.write(s);
        }
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
        try (FileOutputStream out = new FileOutputStream(f,true)) {
            out.write(b);
        }
        return this;
    }
    
    public File getFile() {
        return f;
    }
    
    public String toString() {
        return String.format("FileHandler:{\"file\": \"%s\"}", f.getAbsolutePath());
    }
}
