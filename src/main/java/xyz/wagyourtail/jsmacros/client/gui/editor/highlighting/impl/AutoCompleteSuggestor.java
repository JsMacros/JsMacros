package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import xyz.wagyourtail.StringHashTrie;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.library.LibraryRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoCompleteSuggestor {
    private final StringHashTrie suggestions = new StringHashTrie();
    private final String language;
    private final String method_separator;
    
    public AutoCompleteSuggestor(String language) {
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
        }
        generateSuggestionTree();
    }
    
    private void generateSuggestionTree() {
        LibraryRegistry registry = Core.instance.libraryRegistry;
        Class<? extends BaseLanguage> lang = Core.instance.defaultLang.getClass();
        for (BaseLanguage l : Core.instance.languages) {
            if (l.extension.equals(this.language)) {
                lang = l.getClass();
                break;
            }
        }
        
        Map<String, Class<?>> libs = new HashMap<>();
        registry.libraries.forEach((k,v) -> {
            libs.put(k.value(), v.getClass());
        });
        registry.perExec.forEach((k,v) -> {
            libs.put(k.value(), v);
        });
        registry.perLanguage.getOrDefault(lang, new HashMap<>()).forEach((k,v) -> {
            libs.put(k.value(), v.getClass());
        });
        registry.perExecLanguage.getOrDefault(lang, new HashMap<>()).forEach((k,v) -> {
            libs.put(k.value(), v);
        });
        libs.forEach((k,v) -> {
            for (Method m : v.getDeclaredMethods()) {
                if (!Modifier.isPublic(m.getModifiers()) || Modifier.isStatic(m.getModifiers())) continue;
                String sigBuilder = k + method_separator + m.getName() + "(";
                suggestions.add(sigBuilder);
            }
        });
    }
    
    public Set<String> getSuggestions(String start) {
        return suggestions.getAllWithPrefix(start);
    }
}
