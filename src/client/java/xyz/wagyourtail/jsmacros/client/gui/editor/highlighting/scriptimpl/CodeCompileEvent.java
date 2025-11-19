package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.scriptimpl;

import io.noties.prism4j.Prism4j;
import xyz.wagyourtail.StringHashTrie;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.editor.SelectCursor;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.Prism;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl.TextStyleCompiler;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * "hidden" event for script based code style compiling / linting tasks.
 * remember to {@code consumer.autoWrap()} everything.
 *
 * @author Wagyourtail
 * @since 1.3.1
 */
@Event("CodeRender")
@SuppressWarnings("unused")
public class CodeCompileEvent extends BaseEvent {
    public final SelectCursor cursor;
    public final String code;
    public final String language;
    public final EditorScreen screen;

    /**
     * you are expected to fill this in with text styling, if not filled, nothing will render
     * if the code is an empty string, you are still expected to put an empty string as the first line here
     */
    public final List<TextHelper> textLines = new LinkedList<>();

    /**
     * you are expected to fill this with suggestions for autocomplete created using
     * {@link #createSuggestion(int, String)}
     */
    public final List<AutoCompleteSuggestion> autoCompleteSuggestions = new LinkedList<>();

    /**
     * you are expected to fill this with a method to create right click actions.
     * method should be {@code (index:number) => Map&lt;string,() => void&gt;},
     * meaning it accepts a character index and returns a map of names to actions.
     */
    public MethodWrapper<Integer, Object, Map<String, MethodWrapper<Object, Object, Object, ?>>, ?> rightClickActions;

    public CodeCompileEvent(String code, String language, EditorScreen screen) {
        super(JsMacrosClient.clientCore);
        this.code = code;
        this.language = language;
        this.screen = screen;
        this.cursor = screen.cursor;
    }

    /**
     * @return <a target="_blank" href="https://github.com/noties/Prism4j/blob/75ac3dae6f8eff5b1b0396df3b806f44ce86c484/prism4j/src/main/java/io/noties/prism4j/Prism4j.java#L54">Prism4j's
     * node list</a> you don't have to use it but if you're not compiling your own...
     * peek at the code of {@link TextStyleCompiler} for the default impl for walking the node tree.
     */
    public List<Prism4j.Node> genPrismNodes() {
        return Prism.getNodes(code, language);
    }

    /**
     * Easy access to the {@link Map} object for use with {@link #rightClickActions}
     *
     * @return specifically a {@link LinkedHashMap}
     */
    public Map<?, ?> createMap() {
        return new LinkedHashMap<>();
    }

    /**
     * more convenient access to TextBuilder
     *
     * @return new instance for use with {@link #textLines}
     */
    public TextBuilder createTextBuilder() {
        return new TextBuilder();
    }

    public AutoCompleteSuggestion createSuggestion(int startIndex, String suggestion) {
        return createSuggestion(startIndex, suggestion, null);
    }

    /**
     * @param startIndex  index that is where the suggestion starts from before the already typed part
     * @param suggestion  complete suggestion including the already typed part
     * @param displayText how the text should be displayed in the dropdown, default is suggestion text
     * @return a new suggestion object
     */
    public AutoCompleteSuggestion createSuggestion(int startIndex, String suggestion, TextHelper displayText) {
        if (displayText == null) {
            return new AutoCompleteSuggestion(startIndex, suggestion);
        }
        return new AutoCompleteSuggestion(startIndex, suggestion, displayText.getRaw());
    }

    /**
     * prefix tree data structure written for you, it's a bit intensive to add things to, especially how I wrote it, but
     * lookup times are much better at least on larger data sets,
     * so create a single copy of this for your static autocompletes and don't be re-creating this every time, store it
     * in {@code globalvars}, probably per language
     * <p>
     * or just don't use it, I'm not forcing you to.
     *
     * @return a new {@link StringHashTrie}
     */
    public StringHashTrie createPrefixTree() {
        return new StringHashTrie();
    }

    /**
     * @return {@code key -> hex integer} values for theme data points, this can be used with the prism data for
     * coloring, just have to use {@link TextBuilder#withColor(int, int, int)}
     * on 1.15 and older versions the integer values with be the default color's index so you can directly pass it
     * to {@link TextBuilder#withColor(int)}
     */
    public Map<String, short[]> getThemeData() {
        return JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).getThemeData();
    }

}
