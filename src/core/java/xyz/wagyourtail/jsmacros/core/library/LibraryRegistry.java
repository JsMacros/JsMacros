package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryRegistry {
    private final Core<?, ?> runner;

    public final Map<Library, BaseLibrary> libraries = new LinkedHashMap<>();
    public final Map<Library, Class<? extends PerExecLibrary>> perExec = new LinkedHashMap<>();
    public final Map<Class<? extends BaseLanguage<?, ?>>, Map<Library, PerLanguageLibrary>> perLanguage = new LinkedHashMap<>();
    public final Map<Class<? extends BaseLanguage<?, ?>>, Map<Library, Class<? extends PerExecLanguageLibrary<?, ?>>>> perExecLanguage = new LinkedHashMap<>();

    public LibraryRegistry(Core<?, ?> runner) {
        this.runner = runner;
    }

    public Map<String, BaseLibrary> getLibraries(BaseLanguage<?, ?> language, BaseScriptContext<?> context) {
        Map<String, BaseLibrary> libs = new LinkedHashMap<>();
        libs.putAll(getOnceLibraries(language));
        libs.putAll(getPerExecLibraries(language, context));
        return libs;
    }

    public Map<String, BaseLibrary> getOnceLibraries(BaseLanguage<?, ?> language) {
        Map<String, BaseLibrary> libs = new LinkedHashMap<>();

        for (Map.Entry<Library, BaseLibrary> lib : libraries.entrySet()) {
            if (lib.getKey().languages().length == 0 || Arrays.stream(lib.getKey().languages()).anyMatch(e -> e.equals(language.getClass()))) {
                libs.put(lib.getKey().value(), lib.getValue());
            }
        }

        for (Map.Entry<Class<? extends BaseLanguage<?, ?>>, Map<Library, PerLanguageLibrary>> languageEntry : perLanguage.entrySet()) {
            if (languageEntry.getKey().isAssignableFrom(language.getClass())) {
                for (Map.Entry<Library, PerLanguageLibrary> lib : languageEntry.getValue().entrySet()) {
                    libs.put(lib.getKey().value(), lib.getValue());
                }
            }
        }

        return libs;
    }

    public Map<String, BaseLibrary> getPerExecLibraries(BaseLanguage<?, ?> language, BaseScriptContext<?> context) {
        Map<String, BaseLibrary> libs = new LinkedHashMap<>();

        for (Map.Entry<Library, Class<? extends PerExecLibrary>> lib : perExec.entrySet()) {
            if (lib.getKey().languages().length == 0 || Arrays.stream(lib.getKey().languages()).anyMatch(e -> e.equals(language.getClass()))) {
                try {
                    libs.put(lib.getKey().value(), lib.getValue().getConstructor(BaseScriptContext.class).newInstance(context));
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                         InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate library, ", e);
                }
            }
        }

        for (Map.Entry<Class<? extends BaseLanguage<?, ?>>, Map<Library, Class<? extends PerExecLanguageLibrary<?, ?>>>> languageEntry : perExecLanguage.entrySet()) {
            if (languageEntry.getKey().isAssignableFrom(language.getClass())) {
                for (Map.Entry<Library, Class<? extends PerExecLanguageLibrary<?, ?>>> lib : languageEntry.getValue().entrySet()) {
                    if (Arrays.stream(lib.getKey().languages()).anyMatch(e -> e.equals(language.getClass()))) {
                        try {
                            libs.put(lib.getKey().value(), lib.getValue().getConstructor(context.getClass(), Class.class).newInstance(context, language.getClass()));
                        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                                 InvocationTargetException e) {
                            throw new RuntimeException("Failed to instantiate library, ", e);
                        }
                    }
                }
            }
        }

        return libs;
    }

    public synchronized void addLibrary(Class<? extends BaseLibrary> clazz) {
        if (clazz.isAnnotationPresent(Library.class)) {
            Library ann = clazz.getAnnotation(Library.class);
            if (PerExecLibrary.class.isAssignableFrom(clazz)) {
                perExec.put(ann, clazz.asSubclass(PerExecLibrary.class));
            } else if (PerExecLanguageLibrary.class.isAssignableFrom(clazz)) {
                for (Class<? extends BaseLanguage<?, ?>> lang : ann.languages()) {
                    if (!perExecLanguage.containsKey(lang)) {
                        perExecLanguage.put(lang, new LinkedHashMap<>());
                    }
                    perExecLanguage.get(lang).put(ann, (Class<? extends PerExecLanguageLibrary<?, ?>>) clazz);
                }
            } else if (PerLanguageLibrary.class.isAssignableFrom(clazz)) {
                for (Class<? extends BaseLanguage<?, ?>> lang : ann.languages()) {
                    if (!perLanguage.containsKey(lang)) {
                        perLanguage.put(lang, new LinkedHashMap<>());
                    }
                    try {
                        perLanguage.get(lang).put(ann, clazz.asSubclass(PerLanguageLibrary.class).getConstructor(Core.class, Class.class).newInstance(runner, lang));
                    } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new RuntimeException("Failed to instantiate library, ", e);
                    }
                }
            } else {
                try {
                    libraries.put(ann, clazz.getConstructor(Core.class).newInstance(runner));
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate library, ", e);
                }
            }
        } else {
            throw new RuntimeException("Tried to add library that doesn't have a proper library annotation");
        }
    }

}
