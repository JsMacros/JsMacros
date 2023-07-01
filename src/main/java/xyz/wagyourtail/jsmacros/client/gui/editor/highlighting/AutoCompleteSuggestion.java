package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting;

import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;

public class AutoCompleteSuggestion {
    public final int startIndex;
    public final String suggestion;
    public final Text displayText;

    public AutoCompleteSuggestion(int startIndex, String suggestion) {
        this.suggestion = suggestion;
        this.startIndex = startIndex;
        this.displayText = Text.literal(suggestion).setStyle(EditorScreen.defaultStyle);
    }

    public AutoCompleteSuggestion(int startIndex, String suggestion, Text displayText) {
        this.suggestion = suggestion;
        this.startIndex = startIndex;
        this.displayText = displayText;
    }

}
