package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import io.noties.prism4j.Prism4j;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.backport.TextBackport;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.Prism;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;

public class DefaultCodeCompiler extends AbstractRenderCodeCompiler {
    private final Map<String, short[]> themeData = Core.getInstance().config.getOptions(ClientConfigV2.class).getThemeData();
    private final AutoCompleteSuggester suggester;
    private Text[] compiledText = new Text[0];
    private List<AutoCompleteSuggestion> suggestions = new LinkedList<>();

    public DefaultCodeCompiler(String language, EditorScreen screen) {
        super(language, screen);
        suggester = new AutoCompleteSuggester(language);
    }

    @Override
    public void recompileRenderedText(@NotNull String text) {
        // style compiler
        if (text.length() == 0) {
            compiledText = new Text[]{literal("")};
        } else {
            final List<Prism4j.Node> nodes = Prism.getNodes(text, language);
            final TextStyleCompiler visitor = new TextStyleCompiler(EditorScreen.defaultStyle, themeData);
            visitor.visit(nodes);
            compiledText = visitor.getResult().toArray(new Text[0]);
        }

        // suggestion compile
        if (screen.cursor.startIndex != screen.cursor.endIndex || !Core.getInstance().config.getOptions(ClientConfigV2.class).editorSuggestions || screen.prevChar == '\0') {
            suggestions = new LinkedList<>();
        } else {
            String line = text.split("\n", -1)[screen.cursor.startLine].substring(0, screen.cursor.startLineIndex);
            Matcher m = Pattern.compile(String.format("[\\w%s]+$", language.equals("lua") ? ":" : ".")).matcher(line);
            if (m.find()) {
                String start = m.group();
                Set<String> suggestions = suggester.getSuggestions(start);
                this.suggestions = suggestions.stream().map(e -> new AutoCompleteSuggestion(screen.cursor.startIndex - start.length(), e)).collect(Collectors.toList());
            } else {
                suggestions = new LinkedList<>();
            }
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
        return suggestions;
    }

}
