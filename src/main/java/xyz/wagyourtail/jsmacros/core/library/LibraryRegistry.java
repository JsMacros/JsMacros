package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryRegistry {
    
    public final Map<Library, BaseLibrary> libraries = new LinkedHashMap<>();
    public final Map<Library, Class<? extends BaseLibrary>> perExec = new LinkedHashMap<>();
    public final Map<Class<? extends BaseLanguage>, Map<Library, PerLanguageLibrary>> perLanguage = new LinkedHashMap<>();
    public final Map<Class<? extends BaseLanguage>, Map<Library, Class<? extends PerExecLanguageLibrary>>> perExecLanguage = new LinkedHashMap<>();
    
    public LibraryRegistry() {
    }
    
    public Map<String, BaseLibrary> getLibraries(BaseLanguage language, Object context, Thread thread) {
        Map<String, BaseLibrary> libs = new LinkedHashMap<>();
        for (Map.Entry<Library, BaseLibrary> lib : libraries.entrySet()) {
            if (lib.getKey().languages().length == 0 || !Arrays.stream(lib.getKey().languages()).noneMatch(e -> e.equals(language.getClass())))
                libs.put(lib.getKey().value(), lib.getValue());
        }
        for (Map.Entry<Library, Class<? extends BaseLibrary>> lib : perExec.entrySet()) {
            if (lib.getKey().languages().length == 0 || !Arrays.stream(lib.getKey().languages()).noneMatch(e -> e.equals(language.getClass()))) {
                try {
                    libs.put(lib.getKey().value(), lib.getValue().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate library, ", e);
                }
            }
        }
        for (Map.Entry<Library, PerLanguageLibrary> lib : perLanguage.getOrDefault(language.getClass(), new LinkedHashMap<>()).entrySet()) {
            libs.put(lib.getKey().value(), lib.getValue());
        }
        
        for (Map.Entry<Library, Class<? extends PerExecLanguageLibrary>> lib : perExecLanguage.getOrDefault(language.getClass(), new LinkedHashMap<>()).entrySet()) {
            if (lib.getKey().languages().length > 0 && Arrays.stream(lib.getKey().languages()).noneMatch(e -> e.equals(language.getClass()))) continue;
            else {
                try {
                    libs.put(lib.getKey().value(), lib.getValue().getConstructor(Class.class, Object.class, Thread.class).newInstance(language.getClass(), context, thread));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate library, ", e);
                }
            }
        }
        
        return libs;
    }
    
    public synchronized void addLibrary(Class<? extends BaseLibrary> clazz) {
        if (clazz.isAnnotationPresent(Library.class)) {
            Library ann = clazz.getAnnotation(Library.class);
            if (PerExecLibrary.class.isAssignableFrom(clazz)) {
                perExec.put(ann, clazz);
            } else if (PerExecLanguageLibrary.class.isAssignableFrom(clazz)) {
                for (Class<? extends BaseLanguage> lang : ann.languages()) {
                    if (!perExecLanguage.containsKey(lang)) perExecLanguage.put(lang, new LinkedHashMap<>());
                    perExecLanguage.get(lang).put(ann, clazz.asSubclass(PerExecLanguageLibrary.class));
                }
            } else if (PerLanguageLibrary.class.isAssignableFrom(clazz)) {
                for (Class<? extends BaseLanguage> lang : ann.languages()) {
                    if (!perLanguage.containsKey(lang)) perLanguage.put(lang, new LinkedHashMap<>());
                    try {
                        perLanguage.get(lang).put(ann, clazz.asSubclass(PerLanguageLibrary.class).getConstructor(Class.class).newInstance(lang));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate library, ", e);
                    }
                }
            } else {
                try {
                    libraries.put(ann, clazz.newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate library, ", e);
                }
            }
        } else {
            throw new RuntimeException("Tried to add library that doesn't have a proper library annotation");
        }
    }
}
