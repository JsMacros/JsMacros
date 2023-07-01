package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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
        try (FileWriter out = new FileWriter(f, charset, false)) {
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
        return new String(readBytes(), charset);
    }
    
    /**
     * @since 1.2.6
     * 
     * @return
     * @throws IOException
     */
    public byte[] readBytes() throws IOException {
        try (FileInputStream in = new FileInputStream(f)) {
            if (f.length() > Integer.MAX_VALUE)
                throw new IOException("File is too large to read into memory. (max size: " + Integer.MAX_VALUE + ")");
            byte[] bytes = new byte[(int) f.length()];
            in.read(bytes);
            return bytes;
        }
    }

    /**
     * get an iterator for the lines in the file.
     * please call {@link FileLineIterator#close()} when you are done with the iterator to not leak resources.
     *
     * @since 1.8.4
     * @return
     */
    public FileLineIterator readLines() {
        try {
            return new FileLineIterator(f, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get an input stream for the file.
     * please call {@link BufferedInputStream#close()} when you are done with the stream to not leak resources.
     * @since 1.8.4
     * @return
     */
    public BufferedInputStream streamBytes() {
        try {
            return new BufferedInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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
        try (FileWriter out = new FileWriter(f, charset, true)) {
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

    public static class FileLineIterator implements Iterator<String>, AutoCloseable {
        private final BufferedReader reader;
        private String nextLine;

        public FileLineIterator(File file, Charset charset) throws IOException {
            this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            this.nextLine = reader.readLine();
        }

        @Override
        public boolean hasNext() {
            return nextLine != null;
        }

        @Override
        public String next() {
            String line = nextLine;
            try {
                nextLine = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return line;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }

    }
}
