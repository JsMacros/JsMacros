package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.scriptimpl;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.*;

/**
 * @author Wagyourtail
 */
public class ScriptCodeCompiler extends AbstractRenderCodeCompiler {
    private final ScriptTrigger scriptTrigger;
    private Text[] compiledText = new Text[] {new LiteralText("")};
    private MethodWrapper<Integer, Object, Map<String, MethodWrapper<Object, Object, Object>>> getRClickActions = null;
    private List<AutoCompleteSuggestion> suggestions = new LinkedList<>();
    
    public ScriptCodeCompiler(String language, EditorScreen screen, String scriptFile) {
        super(language, screen);
        scriptTrigger = new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "CodeCompile", scriptFile, true);
    }
    
    @Override
    public void recompileRenderedText(@NotNull String text) {
        CodeCompileEvent compileEvent = new CodeCompileEvent(text, language, screen);
        Thread t = JsMacros.core.exec(scriptTrigger, compileEvent);
        if (t != null) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }
        getRClickActions = compileEvent.rightClickActions;
        compiledText = compileEvent.textLines.stream().map(e -> ((MutableText) e.getRaw()).setStyle(EditorScreen.defaultStyle)).toArray(Text[]::new);
        suggestions = compileEvent.autoCompleteSuggestions;
    }
    
    @NotNull
    @Override
    public Map<String, Runnable> getRightClickOptions(int index) {
        if (getRClickActions == null) return new HashMap<>();
        // lets force this cast, because runtime shouldn't have issues
        Map<String, Runnable> results = (Map) getRClickActions.apply(index);
        if (results == null) return new LinkedHashMap<>();
        return results;
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
