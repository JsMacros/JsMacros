package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import xyz.wagyourtail.StringHashTrie;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.core.extensions.LanguageExtension;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.library.LibraryRegistry;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoCompleteSuggester {
    private final StringHashTrie suggestions = new StringHashTrie();
    private final String language;
    private final String method_separator;

    public AutoCompleteSuggester(String language) {
        switch (language) {
            case "python":
                this.language = ".py";
                this.method_separator = ".";
                break;
            case "lua":
                this.language = ".lua";
                this.method_separator = ":";
                break;
            case "ruby":
                this.language = ".rb";
                this.method_separator = ".";
                break;
            default:
                this.language = ".js";
                this.method_separator = ".";
                break;
        }
        generateSuggestionTree();
    }

    private void generateSuggestionTree() {
        LibraryRegistry registry = JsMacrosClient.clientCore.libraryRegistry;
        LanguageExtension ex = JsMacrosClient.clientCore.extensions.getExtensionForFile(new File(language));
        if (ex == null) {
            ex = JsMacrosClient.clientCore.extensions.getHighestPriorityExtension();
        }
        Class<? extends BaseLanguage> lang = ex.getLanguage(JsMacrosClient.clientCore).getClass();

        Map<String, Class<?>> libs = new HashMap<>();
        registry.libraries.forEach((k, v) -> {
            libs.put(k.value(), v.getClass());
        });
        registry.perExec.forEach((k, v) -> {
            libs.put(k.value(), v);
        });
        registry.perLanguage.getOrDefault(lang, new HashMap<>()).forEach((k, v) -> {
            libs.put(k.value(), v.getClass());
        });
        registry.perExecLanguage.getOrDefault(lang, new HashMap<>()).forEach((k, v) -> {
            libs.put(k.value(), v);
        });
        libs.forEach((k, v) -> {
            for (Method m : v.getDeclaredMethods()) {
                if (!Modifier.isPublic(m.getModifiers()) || Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                String sigBuilder = k + method_separator + m.getName() + "(";
                suggestions.add(sigBuilder);
            }
        });
    }

    public Set<String> getSuggestions(String start) {
        return suggestions.getAllWithPrefixCaseInsensitive(start);
    }

}
