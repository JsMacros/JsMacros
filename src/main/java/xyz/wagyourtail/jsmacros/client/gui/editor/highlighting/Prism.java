package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting;

import io.noties.prism4j.Prism4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Prism {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    
    @NotNull
    public List<Prism4j.Node> getNodes(String text, String language) {
        Prism4j.Grammar grammar = prism4j.grammar(language);
        if (grammar == null) throw new NullPointerException("could not locate grammar definition for language: " + language);
        return prism4j.tokenize(text, grammar);
    }

}
