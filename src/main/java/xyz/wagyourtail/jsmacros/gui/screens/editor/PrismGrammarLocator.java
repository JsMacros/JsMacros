package xyz.wagyourtail.jsmacros.gui.screens.editor;

import com.google.common.collect.Sets;
import io.noties.prism4j.GrammarLocator;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.languages.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PrismGrammarLocator implements GrammarLocator {
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
            default:
                return null;
        }
    }
    
    @NotNull
    @Override
    public Set<String> languages() {
        return Sets.newHashSet("javascript", "lua", "python");
    }
    
}
