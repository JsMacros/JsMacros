package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.scriptimpl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.wagyourgui.overlays.ConfirmOverlay;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Wagyourtail
 */
public class ScriptCodeCompiler extends AbstractRenderCodeCompiler {
    private final ScriptTrigger scriptTrigger;
    private Text[] compiledText = new Text[]{Text.literal("")};
    private MethodWrapper<Integer, Object, Map<String, MethodWrapper<Object, Object, Object, ?>>, ?> getRClickActions = null;
    private List<AutoCompleteSuggestion> suggestions = new LinkedList<>();

    public ScriptCodeCompiler(String language, EditorScreen screen, File scriptFile) {
        super(language, screen);
        scriptTrigger = new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "CodeCompile", scriptFile.toPath(), true, true);
    }

    @Override
    public void recompileRenderedText(@NotNull String text) {
        CodeCompileEvent compileEvent = new CodeCompileEvent(text, language, screen);
        EventContainer<?> t = JsMacrosClient.clientCore.exec(scriptTrigger, compileEvent, null, (ex) -> {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            StringWriter st = new StringWriter();
            ex.printStackTrace(new PrintWriter(st));
            Text error = Text.literal(st.toString().replaceAll("\r", "").replaceAll("\t", "    ")).setStyle(EditorScreen.defaultStyle);
            screen.openOverlay(new ConfirmOverlay(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2, false, renderer, error, screen, (e) -> screen.openParent()));
        });
        if (t != null) {
            try {
                t.awaitLock(null);
            } catch (InterruptedException ignored) {
            }
        }
        getRClickActions = compileEvent.rightClickActions;
        compiledText = compileEvent.textLines.stream().map(e -> ((MutableText) e.getRaw()).setStyle(EditorScreen.defaultStyle)).toArray(Text[]::new);
        suggestions = compileEvent.autoCompleteSuggestions;
    }

    @NotNull
    @Override
    public Map<String, Runnable> getRightClickOptions(int index) {
        if (getRClickActions == null) {
            return new HashMap<>();
        }
        Map<String, ? extends Runnable> results = null;
        try {
            results = getRClickActions.apply(index);
        } catch (Throwable e) {
            JsMacrosClient.clientCore.profile.logError(e);
        }
        if (results == null) {
            return new LinkedHashMap<>();
        }
        return (Map<String, Runnable>) results;
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
