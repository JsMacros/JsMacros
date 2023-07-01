package xyz.wagyourtail.jsmacros.core.extensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface Extension {

    void init();

    int getPriority();

    String getLanguageImplName();

    ExtMatch extensionMatch(File file);

    String defaultFileExtension();

    /**
     * @return a single static instance of the language definition
     */
    BaseLanguage<?, ?> getLanguage(Core<?, ?> runner);

    Set<Class<? extends BaseLibrary>> getLibraries();

    default Set<URL> getDependencies() {
        return getDependenciesInternal(this.getClass(), "jsmacros.ext." + getLanguageImplName() + ".json");
    }

    static Set<URL> getDependenciesInternal(Class<?> clazz, String fname) {
        JsonElement json;
        try (Reader reader = new InputStreamReader(clazz.getResourceAsStream("/" + fname))) {
            json = new JsonParser().parse(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JsonElement dependencies = json.getAsJsonObject().get("dependencies");
        if (dependencies == null) {
            return new HashSet<>();
        }
        String dependenciesString = dependencies.getAsString();
        if (dependenciesString.equals("${dependencies}")) {
            return new HashSet<>();
        }
        String[] dependenciesArray = dependenciesString.split(" ");
        Set<URL> dependenciesSet = new HashSet<>();
        for (String dependency : dependenciesArray) {
            URL resource = clazz.getResource("/META-INF/jsmacrosdeps/" + dependency.trim());
            if (resource != null) {
                dependenciesSet.add(resource);
            } else {
                System.err.println("[JsMacrosExtensionManager] Could not find dependency: " + dependency);
            }
        }
        return dependenciesSet;
    }

    BaseWrappedException<?> wrapException(Throwable t);

    default Map<String, String> getTranslations(String lang) {
        return getTranslationsInternal(this.getClass(), "assets/jsmacros/" + getLanguageImplName() + "/lang/" + lang + ".json");
    }

    static Map<String, String> getTranslationsInternal(Class<?> clazz, String fname) {
        JsonElement json;
        try (Reader reader = new InputStreamReader(clazz.getResourceAsStream("/" + fname))) {
            json = new JsonParser().parse(reader);
        } catch (Exception e) {
            return new HashMap<>();
        }

        if (json.isJsonObject()) {
            return json.getAsJsonObject().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getAsString()));
        }
        return new HashMap<>();
    }

    boolean isGuestObject(Object o);

    enum ExtMatch {
        NOT_MATCH(false),
        MATCH(true),
        MATCH_WITH_NAME(true);

        boolean match;

        ExtMatch(boolean match) {
            this.match = match;
        }

        public boolean isMatch() {
            return match;
        }
    }

}
