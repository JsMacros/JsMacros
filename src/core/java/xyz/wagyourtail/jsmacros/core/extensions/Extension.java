package xyz.wagyourtail.jsmacros.core.extensions;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface Extension {

    String getExtensionName();

    /**
     * @return the *minimum* version of the jsMacros core that this extension is compatible with.
     * @since 1.9.0
     */
    default String minCoreVersion() {
        return "2.0.0";
    }

    /**
     * @return the *maximum* version of the jsMacros core that this extension is compatible with.
     * @since 1.9.0
     * @return
     */
    default String maxCoreVersion() {
        return "2.0.0";
    }

    void init();

    default Set<URL> getDependencies() {
        return getDependenciesInternal(this.getClass(), "jsmacros.ext." + getExtensionName() + ".json");
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

    default Map<String, String> getTranslations(String lang) {
        return getTranslationsInternal(this.getClass(), "assets/jsmacros/" + getExtensionName() + "/lang/" + lang + ".json");
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

}
