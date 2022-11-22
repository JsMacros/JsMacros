package xyz.wagyourtail.jsmacros.core.extensions;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtensionLoader {
    private final Set<Extension> extensions = new HashSet<>();
    private final Core<?, ?> core;

    private ExtensionClassLoader classLoader;

    private Extension highestPriorityExtension;

    private boolean loadingDone;

    private final Path extPath;


    public ExtensionLoader(Core<?, ?> core) {
        this.core = core;
        this.extPath = core.config.configFolder.toPath().resolve("LanguageExtensions");
    }

    public boolean isExtensionLoaded(String name) {
        if (notLoaded()) loadExtensions();
        return extensions.stream().anyMatch(e -> e.getLanguageImplName().equals(name));
    }

    public boolean notLoaded() {
        return !loadingDone;
    }

    public Extension getHighestPriorityExtension() {
        if (notLoaded()) loadExtensions();
        if (highestPriorityExtension == null) {
            highestPriorityExtension = extensions.stream().max(Comparator.comparingInt(Extension::getPriority)).orElse(null);
        }
        return highestPriorityExtension;
    }

    public Set<Extension> getAllExtensions() {
        if (notLoaded()) loadExtensions();
        return extensions;
    }

    public @Nullable Extension getExtensionForFile(File file) {
        if (notLoaded()) loadExtensions();
        List<Pair<Extension.ExtMatch, Extension>> extensions = this.extensions.stream().map(e -> new Pair<>(e.extensionMatch(file), e)).filter(p -> p.getT().isMatch()).collect(Collectors.toList());
        if (extensions.size() > 1) {
            List<Pair<Extension.ExtMatch, Extension>> extensionsByName = extensions.stream().filter(p -> p.getT() == Extension.ExtMatch.MATCH_WITH_NAME).collect(Collectors.toList());
            if (extensionsByName.size() > 0) {
                extensionsByName.sort(Comparator.comparingInt(e -> -e.getU().getPriority()));
                return extensionsByName.get(0).getU();
            }
        }
        if (extensions.size() > 0) {
            extensions.sort(Comparator.comparingInt(e -> -e.getU().getPriority()));
            return extensions.get(0).getU();
        }
        return null;
    }

    public Extension getExtensionForName(String lang) {
        if (notLoaded()) loadExtensions();
        return extensions.stream().filter(e -> e.getLanguageImplName().equals(lang)).findFirst().orElse(null);
    }

    public synchronized void loadExtensions() {
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

        URL[] urls;
        try (Stream<Path> files = Files.list(extPath)) {
            urls = files.filter(Files::isRegularFile).map(e -> {
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

        // extract lib to dependencies folder
        Path dependenciesPath = extPath.resolve("tmp");
        try {
            Files.createDirectories(dependenciesPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // add internal extensions
        Set<URL> internalExtensions = Extension.getDependenciesInternal(ExtensionLoader.class,
            "jsmacros.extension.json"
        );
        for (URL lib : internalExtensions) {
            System.out.println("Adding internal extension: " + lib);
            // copy resource to dependencies folder
            Path path = dependenciesPath.resolve(lib.getPath().substring(lib.getPath().lastIndexOf('/') + 1));
            try (InputStream stream = lib.openStream()){
                Files.write(path, IOUtils.toByteArray(stream), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                System.out.println("Extracted dependency " + path);
                classLoader.addURL(path.toUri().toURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // load extensions
        for (Extension value : ServiceLoader.load(Extension.class, classLoader)) {
            extensions.add(value);
        }

        System.out.println("Loaded " + extensions.size() + " extensions");

        // load extension deps
        for (Extension extension : extensions) {
            Set<URL> deps = extension.getDependencies();
            if (deps.isEmpty()) {
                System.out.println("No dependencies for extension: " + extension.getClass().getName());
            }
            for (URL lib : deps) {
                // copy resource to dependencies folder
                Path path = dependenciesPath.resolve(lib.getPath().substring(lib.getPath().lastIndexOf('/') + 1));
                try (InputStream stream = lib.openStream()){
                    Files.write(path, IOUtils.toByteArray(stream), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    System.out.println("Extracted dependency " + path);
                    classLoader.addURL(path.toUri().toURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Thread.currentThread().setContextClassLoader(classLoader);
        for (Extension extension : extensions) {
            for (Class<? extends BaseLibrary> lib : extension.getLibraries()) {
                core.libraryRegistry.addLibrary(lib);
            }

            extension.init();
        }
        loadingDone = true;
    }

    public boolean isGuestObject(Object obj) {
        if (notLoaded()) loadExtensions();
        return extensions.stream().anyMatch(e -> e.isGuestObject(obj));
    }

}
