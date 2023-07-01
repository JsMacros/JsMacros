package xyz.wagyourtail;

import java.io.*;

/**
 * @author Wagyourtail
 * @since 1.1.8
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
     * writes a string to the file. this is a destructive operation that replaces the file contents.
     *
     * @param s
     * @return
     * @throws IOException
     * @since 1.1.8
     */
    public FileHandler write(String s) throws IOException {
        try (FileWriter out = new FileWriter(f, false)) {
            out.write(s);
        }
        return this;
    }

    /**
     * writes a byte array to the file. this is a destructive operation that replaces the file contents.
     *
     * @param b
     * @return
     * @throws IOException
     * @since 1.1.8
     */
    public FileHandler write(byte[] b) throws IOException {
        try (FileOutputStream out = new FileOutputStream(f, false)) {
            out.write(b);
        }
        return this;
    }

    /**
     * @return
     * @throws IOException
     * @since 1.1.8
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
     * @return
     * @throws IOException
     * @since 1.2.6
     */
    public byte[] readBytes() throws IOException {
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] bytes = new byte[(int) f.length()];
            in.read(bytes);
            return bytes;
        }
    }

    /**
     * @param s
     * @return
     * @throws IOException
     * @since 1.1.8
     */
    public FileHandler append(String s) throws IOException {
        try (FileWriter out = new FileWriter(f, true)) {
            out.write(s);
        }
        return this;
    }

    /**
     * @param b
     * @return
     * @throws IOException
     * @since 1.2.6
     */
    public FileHandler append(byte[] b) throws IOException {
        try (FileOutputStream out = new FileOutputStream(f, true)) {
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
