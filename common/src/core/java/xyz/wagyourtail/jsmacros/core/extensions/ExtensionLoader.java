package xyz.wagyourtail.jsmacros.core.extensions;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ExtensionLoader {
    private static final Set<Extension> extensions = new HashSet<>();

    private static ClassLoader classLoader;

    private static Extension highestPriorityExtension;

    public static synchronized void loadExtensions() {
        if (classLoader != null)
            throw new IllegalStateException("Extensions already loaded");
        Path extPath = Core.getInstance().config.configFolder.toPath().resolve("LanguageExtensions");
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

        classLoader = new URLClassLoader(urls, ExtensionLoader.class.getClassLoader());

        extensions.addAll(ServiceLoader.load(Extension.class, classLoader)
            .stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toSet()));

        for (Extension extension : extensions) {
            for (Class<? extends BaseLibrary> lib : extension.getLibraries()) {
                try {
                    Class.forName(lib.getName(), true, classLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Path lib : extension.getDependencies()) {
                //TODO. jij loading on classLoader
            }
        }
    }

    public static boolean isExtensionLoaded(String name) {
        return extensions.stream().anyMatch(e -> e.getLanguageName().equals(name));
    }

    public static boolean notLoaded() {
        return classLoader == null;
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
