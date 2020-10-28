package xyz.wagyourtail.jsmacros.gui.containers;

import java.util.function.Consumer;

import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui.MacroScreen;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.MultiElementContainer;
import xyz.wagyourtail.jsmacros.profile.Profile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class MacroListTopbar extends MultiElementContainer {
    public MacroScreen parent;
    public RawMacro.MacroType deftype;
    private Consumer<RawMacro> addMacro;
    private Consumer<MacroListTopbar> runFile;
    private Button type;
    
    public MacroListTopbar(MacroScreen parent, int x, int y, int width, int height, TextRenderer textRenderer, RawMacro.MacroType deftype, Consumer<AbstractButtonWidget> addButton, Consumer<RawMacro> addMacro, Consumer<MacroListTopbar>runFile) {
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
        
        addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 3, JsMacros.config.options.sortMethod == RawMacro.SortMethod.Enabled ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.enabled"), (btn) -> {
            JsMacros.config.setSortComparator(RawMacro.SortMethod.Enabled);
            parent.reload();
        }));
        
        type = (Button) addButton(new Button(x + w / 12 + 1, y + 1, (w / 4) - (w / 12) - 1, height - 3, JsMacros.config.options.sortMethod == RawMacro.SortMethod.TriggerName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText(deftype == IRawMacro.MacroType.EVENT ? "jsmacros.events" : "jsmacros.keys"), (btn) -> {
            JsMacros.config.setSortComparator(RawMacro.SortMethod.TriggerName);
            parent.reload();
        }));
        
        addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 3, JsMacros.config.options.sortMethod == RawMacro.SortMethod.FileName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.file"), (btn) -> {
            JsMacros.config.setSortComparator(RawMacro.SortMethod.FileName);
            parent.reload();
        }));
        
        addButton(new Button(x + w - 32, y + 1, 30, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.run"), (btn) -> {
            if (runFile != null) runFile.accept(this);
        }));
        
        addButton(new Button(x + w - 1, y+1, 11, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("+"), (btn) -> {
            RawMacro macro = new RawMacro(deftype, "", "", false);
            Profile.registry.addRawMacro(macro);
            addMacro.accept(macro);
        }));
    }

    public void updateType(RawMacro.MacroType type) {
        this.deftype = type;
        this.type.setMessage(new TranslatableText(deftype == IRawMacro.MacroType.EVENT ? "jsmacros.events" : "jsmacros.keys"));
    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        fill(matricies, x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(matricies, x, y + height - 2, x + width, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x, y + height - 1, x + width, y + height, 0xFF7F7F7F);
        fill(matricies, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        int w = this.width - 12;
        
        fill(matricies, x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    }
}
