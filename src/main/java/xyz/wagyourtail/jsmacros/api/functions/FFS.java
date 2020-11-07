package xyz.wagyourtail.jsmacros.api.functions;

import com.google.common.io.Files;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.classes.FileHandler;
import xyz.wagyourtail.jsmacros.extensionbase.Functions;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Better File-System functions.
 * 
 * An instance of this class is passed to scripts as the {@code fs} variable.
 * 
 * @since 1.1.8
 * 
 * @author Wagyourtail
 *
 */
public class FFS extends Functions {

    public FFS(String libName) {
        super(libName);
    }
    
    public FFS(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }

    /**
     * List files in path.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return An array of file names as {@link java.lang.String Strings}.
     */
    public String[] list(String path) {
        return new File(JsMacros.config.macroFolder, path).list();
    }
    
    /**
     * Check if a file exists.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return
     */
    public boolean exists(String path) {
        return new File(JsMacros.config.macroFolder, path).exists();
    }
    
    /**
     * Check if a file is a directory.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return
     */
    public boolean isDir(String path) {
        return new File(JsMacros.config.macroFolder, path).isDirectory();
    }
    
    /**
     * Get the last part (name) of a file.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return a {@link java.lang.String String} of the file name.
     */
    public String getName(String path) {
        return new File(JsMacros.config.macroFolder, path).getName();
    }
    
    /**
     * Make a directory.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     */
    public boolean makeDir(String path) {
        return new File(JsMacros.config.macroFolder, path).mkdir();
    }
    
    /**
     * Move a file.
     * 
     * @since 1.1.8
     * 
     * @param from relative to the macro folder.
     * @param to relative to the macro folder.
     * @throws IOException
     */
    public void move(String from, String to) throws IOException {
        Files.move(new File(JsMacros.config.macroFolder, from), new File(JsMacros.config.macroFolder, to));
    }
    
    /**
     * Copy a file.
     * 
     * @since 1.1.8
     * 
     * @param from relative to the macro folder.
     * @param to relative to the macro folder.
     * @throws IOException
     */
    public void copy(String from, String to) throws IOException {
        Files.copy(new File(JsMacros.config.macroFolder, from), new File(JsMacros.config.macroFolder, to));
    }
    
    /**
     * Delete a file.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     */
    public boolean delete(String path) {
        return new File(JsMacros.config.macroFolder, path).delete();
    }
    
    /**
     * Combine 2 paths.
     * 
     * @since 1.1.8
     * 
     * @param patha path is relative to the macro folder.
     * @param pathb
     * @return a {@link java.lang.String String} of the combined path.
     * @throws IOException
     */
    public String combine(String patha, String pathb) throws IOException {
        String f = new File(new File(JsMacros.config.macroFolder, patha), pathb).getCanonicalPath().substring(JsMacros.config.macroFolder.getCanonicalPath().length());
        if (f.startsWith("/")) return "."+f;
        return f;
    }
    
    
    /**
     * Gets the directory part of a file path, or the parent directory of a folder.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return a {@link java.lang.String String} of the combined path.
     * @throws IOException
     */
    public String getDir(String path) throws IOException {
        String f = new File(JsMacros.config.macroFolder, path).getParentFile().getCanonicalPath().substring(JsMacros.config.macroFolder.getCanonicalPath().length());
        if (f.startsWith("/")) return "."+f;
        return f;
    }
    
    /**
     * Open a FileHandler for the file at the specified path.
     * 
     * @since 1.1.8
     * 
     * @see xyz.wagyourtail.jsmacros.api.classes.FileHandler
     * 
     * @param path relative to the macro folder.
     * @return a {@link xyz.wagyourtail.jsmacros.api.classes.FileHandler FileHandler} for the file path.
     */
    public FileHandler open(String path) {
        return new FileHandler(new File(JsMacros.config.macroFolder, path));
    }
}
