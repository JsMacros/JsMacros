package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.io.Files;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.FileHandler;

import java.io.File;
import java.io.IOException;

/**
 * Better File-System functions.
 * 
 * An instance of this class is passed to scripts as the {@code FS} variable.
 * 
 * @since 1.1.8
 * 
 * @author Wagyourtail
 */
 @Library("FS")
 @SuppressWarnings("unused")
public class FFS extends BaseLibrary {

    /**
     * List files in path.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     * @return An array of file names as {@link java.lang.String Strings}.
     */
    public String[] list(String path) {
        return new File(Core.instance.config.macroFolder, path).list();
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
        return new File(Core.instance.config.macroFolder, path).exists();
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
        return new File(Core.instance.config.macroFolder, path).isDirectory();
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
        return new File(Core.instance.config.macroFolder, path).getName();
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
        return new File(Core.instance.config.macroFolder, path).mkdir();
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
        Files.move(new File(Core.instance.config.macroFolder, from), new File(Core.instance.config.macroFolder, to));
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
        Files.copy(new File(Core.instance.config.macroFolder, from), new File(Core.instance.config.macroFolder, to));
    }
    
    /**
     * Delete a file.
     * 
     * @since 1.2.9
     * 
     * @param path relative to the macro folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     */
    public boolean unlink(String path) {
        return new File(Core.instance.config.macroFolder, path).delete();
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
        String f = new File(new File(Core.instance.config.macroFolder, patha), pathb).getCanonicalPath().substring(Core.instance.config.macroFolder.getCanonicalPath().length());
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
        String f = new File(Core.instance.config.macroFolder, path).getParentFile().getCanonicalPath().substring(Core.instance.config.macroFolder.getCanonicalPath().length());
        if (f.startsWith("/")) return "."+f;
        return f;
    }
    
    /**
     * Open a FileHandler for the file at the specified path.
     * 
     * @since 1.1.8
     * 
     * @see FileHandler
     * 
     * @param path relative to the macro folder.
     * @return a {@link FileHandler FileHandler} for the file path.
     */
    public FileHandler open(String path) {
        return new FileHandler(new File(Core.instance.config.macroFolder, path));
    }
}
