package xyz.wagyourtail.jsmacros.core.extensions;

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
        //TODO: create classloader
        extensions.addAll(ServiceLoader.load(Extension.class, classLoader)
            .stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toSet()));
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
