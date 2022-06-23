package xyz.wagyourtail.jsmacros.core.extensions;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ExtensionLoader {
    private static final Set<Extension> extensions = new HashSet<>();

    private static ExtensionClassLoader classLoader;

    private static Extension highestPriorityExtension;

    private static boolean loadingDone;

    private static Path extPath = Core.getInstance().config.configFolder.toPath().resolve("LanguageExtensions");

    public static synchronized void loadExtensions() {
        if (classLoader != null) {
            System.err.println("Extensions already loaded");
            return;
        }
        if (!Files.exists(extPath)) {
            try {
                Files.createDirectories(extPath);
            } catch (Exception e) {
                throw new RuntimeException("Could not create LanguageExtensions directory", e);
            }
        }

        URL[] urls = new URL[0];
        try {
            urls = Files.list(extPath).filter(Files::isRegularFile).map(e -> {
                try {
                    return e.toUri().toURL();
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            }).toArray(URL[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        classLoader = new ExtensionClassLoader(urls);

        // add internal extensions
        Set<URL> internalExtensions = Extension.getDependenciesInternal(ExtensionLoader.class, "jsmacros.extension.json");
        for (URL lib : internalExtensions) {
            System.out.println("Adding internal extension: " + lib);
            // extract lib to dependencies folder
            Path dependenciesPath = extPath.resolve("dependencies");
            if (!Files.exists(dependenciesPath)) {
                try {
                    Files.createDirectories(dependenciesPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // copy resource to dependencies folder
            Path path = dependenciesPath.resolve(lib.getPath().substring(lib.getPath().lastIndexOf('/') + 1));
            try (InputStream stream = lib.openStream()){
                Files.write(path, stream.readAllBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                System.out.println("Extracted dependency " + path);
                classLoader.addURL(path.toUri().toURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // load extensions
        extensions.addAll(ServiceLoader.load(Extension.class, classLoader)
            .stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toSet()));

        System.out.println("Loaded " + extensions.size() + " extensions");

        // load extension deps
        for (Extension extension : extensions) {
            for (Class<? extends BaseLibrary> lib : extension.getLibraries()) {
                Core.getInstance().libraryRegistry.addLibrary(lib);
            }
            Set<URL> deps = extension.getDependencies();
            if (deps.isEmpty()) {
                System.out.println("No dependencies for extension: " + extension.getClass().getName());
            }
            for (URL lib : deps) {
                // extract lib to dependencies folder
                Path dependenciesPath = extPath.resolve("dependencies");
                if (!Files.exists(dependenciesPath)) {
                    try {
                        Files.createDirectories(dependenciesPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                // copy resource to dependencies folder
                Path path = dependenciesPath.resolve(lib.getPath().substring(lib.getPath().lastIndexOf('/') + 1));
                try (InputStream stream = lib.openStream()){
                    Files.write(path, stream.readAllBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    System.out.println("Extracted dependency " + path);
                    classLoader.addURL(path.toUri().toURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (Extension extension : extensions) {
            extension.init();
        }
        loadingDone = true;
    }

    public static boolean isExtensionLoaded(String name) {
        if (notLoaded()) loadExtensions();
        return extensions.stream().anyMatch(e -> e.getLanguageName().equals(name));
    }

    public static boolean notLoaded() {
        return !loadingDone;
    }

    public static Extension getHighestPriorityExtension() {
        if (notLoaded()) loadExtensions();
        if (highestPriorityExtension == null) {
            highestPriorityExtension = extensions.stream().max(Comparator.comparingInt(Extension::getPriority)).orElse(null);
        }
        return highestPriorityExtension;
    }

    public static Set<Extension> getAllExtensions() {
        if (notLoaded()) loadExtensions();
        return extensions;
    }

    public static Extension getExtensionForFileName(String file) {
        List<Extension> extensions = ExtensionLoader.extensions.stream().filter(e -> file.endsWith(e.getLanguageExtension())).collect(Collectors.toList());
        if (extensions.size() > 1) {
            Optional<Extension> ext = extensions.stream().filter(e -> file.endsWith(e.getLanguageName() + "." + e.getLanguageExtension())).findFirst();
            // get max priority extension for language
            return ext.orElseGet(() -> extensions.stream()
                .max(Comparator.comparingInt(Extension::getPriority))
                .orElse(getHighestPriorityExtension()));
        }
        return extensions.isEmpty() ? getHighestPriorityExtension() : extensions.get(0);
    }

    public static Extension getExtensionForName(String lang) {
        return extensions.stream().filter(e -> e.getLanguageName().equals(lang)).findFirst().orElse(getExtensionForFileName(lang));
    }

    public static Extension getExtensionForNameNoDefault(String lang) {
        return extensions.stream().filter(e -> e.getLanguageName().equals(lang)).findFirst().orElse(null);
    }
}
