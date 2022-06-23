package xyz.wagyourtail.jsmacros.core.extensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public interface Extension {

    void init();

    int getPriority();

    String getLanguageName();

    String getLanguageExtension();

    /**
     *
     * @return a single static instance of the language definition
     */
    BaseLanguage<?> getLanguage(Core<?, ?> runner);

    Set<Class<? extends BaseLibrary>> getLibraries();

    default Set<URL> getDependencies() {
        return getDependenciesInternal(this.getClass(), "jsmacros.ext." + getLanguageName() + "." + getLanguageExtension() + ".json");
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
            }
        }
        return dependenciesSet;
    }

    BaseWrappedException<?> wrapException(Throwable t);
}
