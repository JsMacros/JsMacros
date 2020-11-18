package xyz.wagyourtail.jsmacros.gui.elements.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.RunScript;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.screens.macros.MacroScreen;

import java.util.function.Consumer;

public class MacroListTopbar extends MultiElementContainer {
    public MacroScreen parent;
    public IEventTrigger.TriggerType deftype;
    private Consumer<ScriptTrigger> addMacro;
    private Runnable runFile;
    private Button type;
    
    public MacroListTopbar(MacroScreen parent, int x, int y, int width, int height, TextRenderer textRenderer, IEventTrigger.TriggerType deftype, Consumer<AbstractButtonWidget> addButton, Consumer<ScriptTrigger> addMacro, Runnable runFile) {
        super(x, y, width, height, textRenderer, addButton);
        this.deftype = deftype;
        this.addMacro = addMacro;
        this.runFile = runFile;
        this.parent = parent;
        init();
    }

    public void init() {
        super.init();
        
        int w = width - 12;
        
        addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 3, ConfigManager.INSTANCE.options.sortMethod == ScriptTrigger.SortMethod.Enabled ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.enabled"), (btn) -> {
            ConfigManager.INSTANCE.setSortComparator(ScriptTrigger.SortMethod.Enabled);
            parent.reload();
        }));
        
        type = (Button) addButton(new Button(x + w / 12 + 1, y + 1, (w / 4) - (w / 12) - 1, height - 3, ConfigManager.INSTANCE.options.sortMethod == ScriptTrigger.SortMethod.TriggerName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText(deftype == IEventTrigger.TriggerType.EVENT ? "jsmacros.events" : "jsmacros.keys"), (btn) -> {
            ConfigManager.INSTANCE.setSortComparator(ScriptTrigger.SortMethod.TriggerName);
            parent.reload();
        }));
        
        addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 3, ConfigManager.INSTANCE.options.sortMethod == ScriptTrigger.SortMethod.FileName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.file"), (btn) -> {
            ConfigManager.INSTANCE.setSortComparator(ScriptTrigger.SortMethod.FileName);
            parent.reload();
        }));
        
        addButton(new Button(x + w - 32, y + 1, 30, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.run"), (btn) -> {
            if (runFile != null) runFile.run();
        }));
        
        addButton(new Button(x + w - 1, y+1, 11, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("+"), (btn) -> {
            ScriptTrigger macro = new ScriptTrigger(deftype, "", "", false);
            RunScript.eventRegistry.addRawMacro(macro);
            addMacro.accept(macro);
        }));
    }

    public void updateType(IEventTrigger.TriggerType type) {
        this.deftype = type;
        this.type.setMessage(new TranslatableText(deftype == IEventTrigger.TriggerType.EVENT ? "jsmacros.events" : "jsmacros.keys"));
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(matrices, x, y + height - 2, x + width, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x, y + height - 1, x + width, y + height, 0xFF7F7F7F);
        fill(matrices, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        int w = this.width - 12;
        
        fill(matrices, x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    }
}
