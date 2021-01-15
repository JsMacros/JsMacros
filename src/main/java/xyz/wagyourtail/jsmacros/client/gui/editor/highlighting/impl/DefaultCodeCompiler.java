package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import io.noties.prism4j.Prism4j;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.Prism;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;

import java.util.*;

public class DefaultCodeCompiler extends AbstractRenderCodeCompiler {
    private static final Prism prism = new Prism();
    private LiteralText[] compiledText = new LiteralText[0];
    
    
    public DefaultCodeCompiler(String language, EditorScreen screen) {
        super(language, screen);
    }
    
    @Override
    public void recompileRenderedText(@NotNull String text) {
        if (text.length() == 0) {
            compiledText = new LiteralText[] {new LiteralText("")};
        } else {
            final List<Prism4j.Node> nodes = prism.getNodes(text, language);
            final TextStyleCompiler visitor = new TextStyleCompiler(EditorScreen.defaultStyle);
            visitor.visit(nodes);
            compiledText = visitor.getResult().toArray(new LiteralText[0]);
        }
    }
    
    @NotNull
    @Override
    public Map<String, Runnable> getRightClickOptions(int index) {
        return new LinkedHashMap<>();
    }
    
    @NotNull
    @Override
    public Text[] getRenderedText() {
        return compiledText;
    }
    
    @NotNull
    @Override
    public List<AutoCompleteSuggestion> getSuggestions() {
        return new LinkedList<>();
    }
    
}
