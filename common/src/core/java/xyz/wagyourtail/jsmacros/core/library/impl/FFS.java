package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.io.Files;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.FileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
public class FFS extends PerExecLibrary {

    public FFS(BaseScriptContext<?> context) {
        super(context);
    }

    /**
     * List files in path.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     * @return An array of file names as {@link java.lang.String Strings}.
     */
    public String[] list(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().list();
    }
    
    /**
     * Check if a file exists.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     * @return
     */
    public boolean exists(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().exists();
    }
    
    /**
     * Check if a file is a directory.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     * @return
     */
    public boolean isDir(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().isDirectory();
    }
    
    /**
     * Get the last part (name) of a file.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     * @return a {@link java.lang.String String} of the file name.
     */
    public String getName(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().getName();
    }
    
    /**
     * Make a directory.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     */
    public boolean makeDir(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().mkdir();
    }
    
    /**
     * Move a file.
     * 
     * @since 1.1.8
     * 
     * @param from relative to the script's folder.
     * @param to relative to the script's folder.
     * @throws IOException
     */
    public void move(String from, String to) throws IOException {
        Files.move(ctx.getContainedFolder().toPath().resolve(from).toFile(), ctx.getContainedFolder().toPath().resolve(to).toFile());
    }
    
    /**
     * Copy a file.
     * 
     * @since 1.1.8
     * 
     * @param from relative to the script's folder.
     * @param to relative to the script's folder.
     * @throws IOException
     */
    public void copy(String from, String to) throws IOException {
        Files.copy(ctx.getContainedFolder().toPath().resolve(from).toFile(), ctx.getContainedFolder().toPath().resolve(to).toFile());
    }
    
    /**
     * Delete a file.
     * 
     * @since 1.2.9
     * 
     * @param path relative to the script's folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     */
    public boolean unlink(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().delete();
    }
    
    /**
     * Combine 2 paths.
     * 
     * @since 1.1.8
     * 
     * @param patha path is relative to the script's folder.
     * @param pathb
     * @return a {@link java.lang.String String} of the combined path.
     */
    public String combine(String patha, String pathb) {
        return Paths.get(patha, pathb).toString();
    }
    
    
    /**
     * Gets the directory part of a file path, or the parent directory of a folder.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     * @return a {@link java.lang.String String} of the combined path.
     * @throws IOException
     */
    public String getDir(String path) {
        File dir = ctx.getContainedFolder().toPath().resolve(path).toFile().getParentFile();
        return ctx.getContainedFolder().toPath().relativize(dir.toPath()).toString();
    }
    
    /**
     * Open a FileHandler for the file at the specified path.
     * 
     * @since 1.1.8
     * 
     * @see FileHandler
     * 
     * @param path relative to the script's folder.
     * @return a {@link FileHandler FileHandler} for the file path.
     */
    public FileHandler open(String path) {
        return new FileHandler(ctx.getContainedFolder().toPath().resolve(path).toFile());
    }
}
