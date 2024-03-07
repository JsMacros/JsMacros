package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.io.Files;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.FileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * Better File-System functions.
 * <p>
 * An instance of this class is passed to scripts as the {@code FS} variable.
 *
 * @author Wagyourtail
 * @since 1.1.8
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
     * @param path relative to the script's folder.
     * @return An array of file names as {@link java.lang.String Strings}.
     * @since 1.1.8
     */
    @Nullable
    public String[] list(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().list();
    }

    /**
     * Check if a file exists.
     *
     * @param path relative to the script's folder.
     * @return
     * @since 1.1.8
     */
    public boolean exists(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().exists();
    }

    /**
     * Check if a file is a directory.
     *
     * @param path relative to the script's folder.
     * @return
     * @since 1.1.8
     */
    public boolean isDir(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().isDirectory();
    }

    /**
     * @param path the path relative to the script's folder
     * @return {@code true} if the path leads to a file, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFile(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().isFile();
    }

    /**
     * Get the last part (name) of a file.
     *
     * @param path relative to the script's folder.
     * @return a {@link java.lang.String String} of the file name.
     * @since 1.1.8
     */
    public String getName(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().getName();
    }

    /**
     * @param absolutePath the absolute path to the file
     * @return a path relative to the script's folder to the given absolute path.
     * @since 1.8.4
     */
    public String toRelativePath(String absolutePath) {
        return ctx.getContainedFolder().toPath().relativize(Paths.get(absolutePath)).toString();
    }

    /**
     * Creates a new file in the specified path, relative to the script's folder. This will only
     * work if the parent directory already exists. See {@link #createFile(String, String, boolean)}
     * to automatically create all parent directories.
     *
     * @param path the path relative to the script's folder
     * @param name the name of the file
     * @return {@code true} if the file was created successfully, {@code false} otherwise.
     * @throws IOException if there occurs an error while creating the file.
     * @since 1.8.4
     */
    public boolean createFile(String path, String name) throws IOException {
        return createFile(path, name, false);
    }

    /**
     * Creates a new file in the specified path, relative to the script's folder. Optionally parent
     * directories can be created if they do not exist.
     *
     * @param path       the path relative to the script's folder
     * @param name       the name of the file
     * @param createDirs whether to create parent directories if they do not exist or not
     * @return {@code true} if the file was created successfully, {@code false} otherwise.
     * @throws IOException if there occurs an error while creating the file.
     * @since 1.8.4
     */
    public boolean createFile(String path, String name, boolean createDirs) throws IOException {
        File file = ctx.getContainedFolder().toPath().resolve(path).resolve(name).toFile();
        if (createDirs) {
            file.getParentFile().mkdirs();
        }
        return file.createNewFile();
    }

    /**
     * Make a directory.
     *
     * @param path relative to the script's folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     * @since 1.1.8
     */
    public boolean makeDir(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().mkdir();
    }

    /**
     * Move a file.
     *
     * @param from relative to the script's folder.
     * @param to   relative to the script's folder.
     * @throws IOException
     * @since 1.1.8
     */
    public void move(String from, String to) throws IOException {
        Files.move(ctx.getContainedFolder().toPath().resolve(from).toFile(), ctx.getContainedFolder().toPath().resolve(to).toFile());
    }

    /**
     * Copy a file.
     *
     * @param from relative to the script's folder.
     * @param to   relative to the script's folder.
     * @throws IOException
     * @since 1.1.8
     */
    public void copy(String from, String to) throws IOException {
        Files.copy(ctx.getContainedFolder().toPath().resolve(from).toFile(), ctx.getContainedFolder().toPath().resolve(to).toFile());
    }

    /**
     * Delete a file.
     *
     * @param path relative to the script's folder.
     * @return a {@link java.lang.Boolean boolean} for success.
     * @since 1.2.9
     */
    public boolean unlink(String path) {
        return ctx.getContainedFolder().toPath().resolve(path).toFile().delete();
    }

    /**
     * Combine 2 paths.
     *
     * @param patha path is relative to the script's folder.
     * @param pathb
     * @return a {@link java.lang.String String} of the combined path.
     * @throws IOException
     * @since 1.1.8
     */
    public String combine(String patha, String pathb) {
        return Paths.get(patha, pathb).toString();
    }

    /**
     * Gets the directory part of a file path, or the parent directory of a folder.
     *
     * @param path relative to the script's folder.
     * @return a {@link java.lang.String String} of the combined path.
     * @throws IOException
     * @since 1.1.8
     */
    public String getDir(String path) {
        File dir = ctx.getContainedFolder().toPath().resolve(path).toFile().getParentFile();
        return ctx.getContainedFolder().toPath().relativize(dir.toPath()).toString();
    }

    /**
     * Open a FileHandler for the file at the specified path.
     *
     * @param path relative to the script's folder.
     * @return a {@link FileHandler FileHandler} for the file path.
     * @see FileHandler
     * @since 1.1.8
     */
    public FileHandler open(String path) {
        return new FileHandler(ctx.getContainedFolder().toPath().resolve(path).toFile());
    }

    /**
     * Open a FileHandler for the file at the specified path.
     *
     * @param path    relative to the script's folder.
     * @param charset the charset to use for reading/writing the file (default is UTF-8)
     * @return a {@link FileHandler FileHandler} for the file path.
     * @see FileHandler
     * @since 1.8.4
     */
    public FileHandler open(String path, String charset) {
        return new FileHandler(ctx.getContainedFolder().toPath().resolve(path).toFile(), charset);
    }

    /**
     * An advanced method to walk a directory tree and get some information about the files, as well
     * as their paths.
     *
     * @param path        the relative path of the directory to walk through
     * @param maxDepth    the maximum depth to follow, can cause stack overflow if too high
     * @param followLinks whether to follow symbolic links
     * @param visitor     the visitor that is called for each file with the path of the file and its
     *                    attributes
     * @throws IOException
     * @since 1.8.4
     */
    public void walkFiles(String path, int maxDepth, boolean followLinks, MethodWrapper<String, BasicFileAttributes, ?, ?> visitor) throws IOException {
        FileVisitOption[] options = followLinks ? new FileVisitOption[]{FileVisitOption.FOLLOW_LINKS} : new FileVisitOption[0];
        try (Stream<Path> paths = java.nio.file.Files.walk(ctx.getContainedFolder().toPath().resolve(path), maxDepth, options)) {
            paths.forEach(p -> {
                try {
                    visitor.apply(p.relativize(ctx.getContainedFolder().toPath()).toFile().getPath(), java.nio.file.Files.readAttributes(p, BasicFileAttributes.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * @param path the relative path to get the file object for
     * @return the file object for the specified path.
     * @since 1.8.4
     */
    public File toRawFile(String path) {
        return toRawPath(path).toFile();
    }

    /**
     * @param path the relative path to get the path object for
     * @return the path object for the specified path.
     * @since 1.8.4
     */
    public Path toRawPath(String path) {
        return ctx.getContainedFolder().toPath().resolve(path);
    }

    /**
     * @param path the path relative to the script's folder
     * @return the attributes of the file at the specified path.
     * @throws IOException
     * @since 1.8.4
     */
    public BasicFileAttributes getRawAttributes(String path) throws IOException {
        return java.nio.file.Files.readAttributes(ctx.getContainedFolder().toPath().resolve(path), BasicFileAttributes.class);
    }

}
