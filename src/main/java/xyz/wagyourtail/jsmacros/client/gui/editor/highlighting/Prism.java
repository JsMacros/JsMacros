package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting;

import com.google.common.collect.Sets;
import io.noties.prism4j.GrammarLocator;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.languages.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class Prism implements GrammarLocator {
    private final static Prism4j prism4j = new Prism4j(new Prism());

    @NotNull
    public static List<Prism4j.Node> getNodes(String text, String language) {
        Prism4j.Grammar grammar = prism4j.grammar(language);
        if (grammar == null) {
            throw new NullPointerException("could not locate grammar definition for language: " + language);
        }
        return prism4j.tokenize(text, grammar);
    }

    @Nullable
    @Override
    public Prism4j.Grammar grammar(@NotNull Prism4j prism4j, @NotNull String language) {
        switch (language) {
            case "javascript":
                return Prism_javascript.create(prism4j);
            case "lua":
                return Prism_lua.create(prism4j);
            case "python":
                return Prism_python.create(prism4j);
            case "clike":
                return Prism_clike.create(prism4j);
            case "regex":
                return Prism_regex.create(prism4j);
            case "json":
                return Prism_json.create(prism4j);
            case "ruby":
                return Prism_ruby.create(prism4j);
            case "typescript":
                return Prism_typescript.create(prism4j);
            case "groovy":
                return Prism_groovy.create(prism4j);
            case "kotlin":
                return Prism_kotlin.create(prism4j);
            default:
                return null;
        }
    }

    @NotNull
    @Override
    public Set<String> languages() {
        return Sets.newHashSet(
                "javascript",
                "lua",
                "python",
                "clike",
                "regex",
                "json",
                "ruby",
                "typescript",
                "groovy",
                "kotlin"
        );
    }

}
