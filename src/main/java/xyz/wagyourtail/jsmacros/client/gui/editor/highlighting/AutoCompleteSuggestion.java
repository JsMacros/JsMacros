package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting;

public class AutoCompleteSuggestion {
    public final int startIndex;
    public final String suggestion;
    
    public AutoCompleteSuggestion(int startIndex, String suggestion) {
        this.suggestion = suggestion;
        this.startIndex = startIndex;
    }
    
}
