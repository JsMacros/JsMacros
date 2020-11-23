package xyz.wagyourtail.jsmacros.core.library;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LibraryRegistry {
    public final Map<Library, BaseLibrary> libraries = new LinkedHashMap<>();
    public final Map<Library, Class<? extends BaseLibrary>> perExec = new LinkedHashMap();
    
    public LibraryRegistry() {
        //add core libs here
    }
    
    public Map<String, BaseLibrary> getLibraries(String language) {
        Map<String, BaseLibrary> libs = new LinkedHashMap<>();
        for (Map.Entry<Library, BaseLibrary> lib : libraries.entrySet()) {
            if (lib.getKey().onlyAllow().length > 0 && Arrays.stream(lib.getKey().onlyAllow()).noneMatch(e -> e.equals(language))) continue;
            else libs.put(lib.getKey().value(), lib.getValue());
        }
        for (Map.Entry<Library, Class<? extends BaseLibrary>> lib : perExec.entrySet()) {
            if (lib.getKey().onlyAllow().length > 0 && Arrays.stream(lib.getKey().onlyAllow()).noneMatch(e -> e.equals(language))) continue;
            else {
                try {
                    libs.put(lib.getKey().value(), lib.getValue().newInstance());
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
            if (ann.perExec()) {
                perExec.put(ann, clazz);
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
