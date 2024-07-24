package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NoStyleCodeCompiler extends AbstractRenderCodeCompiler {
    private Text[] compiledText = new Text[0];

    public NoStyleCodeCompiler(String language, EditorScreen screen) {
        super(language, screen);
    }

    @Override
    public void recompileRenderedText(@NotNull String text) {
        if (text.length() == 0) {
            compiledText = new Text[]{Text.literal("")};
        } else {
            String[] t2 = text.split("\n");
            compiledText = new Text[t2.length];
            for (int i = 0; i < t2.length; i++) {
                compiledText[i] = Text.literal(t2[i]).setStyle(EditorScreen.defaultStyle);
            }
        }
    }

    @Override
    public @NotNull Map<String, Runnable> getRightClickOptions(int index) {
        return new LinkedHashMap<>();
    }

    @Override
    public @NotNull Text[] getRenderedText() {
        return compiledText;
    }

    @Override
    public @NotNull List<AutoCompleteSuggestion> getSuggestions() {
        return new ArrayList<>();
    }

}
