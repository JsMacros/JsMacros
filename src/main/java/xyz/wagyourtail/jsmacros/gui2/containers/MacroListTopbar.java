package xyz.wagyourtail.jsmacros.gui2.containers;

import java.util.function.Consumer;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MultiElementContainer;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class MacroListTopbar extends MultiElementContainer {
    public MacroEnum deftype;
    private Consumer<RawMacro> addMacro;
    private Consumer<MacroListTopbar> runFile;
    private String enabled;
    private String type;
    private String file;
    
    public MacroListTopbar(int x, int y, int width, int height, TextRenderer textRenderer, MacroEnum deftype, Consumer<AbstractButtonWidget> addButton, Consumer<RawMacro> addMacro, Consumer<MacroListTopbar>runFile) {
        super(x, y, width, height, textRenderer, addButton);
        this.deftype = deftype;
        this.addMacro = addMacro;
        this.runFile = runFile;
        init();
    }

    public void init() {
        super.init();
        enabled = I18n.translate("Enabled");
        type = I18n.translate(deftype == MacroEnum.EVENT ? "jsmacros.events" : "jsmacros.keys");
        file = I18n.translate("jsmacros.file");
        
        int w = width - 12;
        
        addButton(new Button(x + w - 32, y + 1, 30, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("jsmacros.run"), (btn) -> {
            if (runFile != null) runFile.accept(this);
        }));
        
        addButton(new Button(x + w - 1, y+1, 11, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("+"), (btn) -> {
            RawMacro macro = new RawMacro(deftype, "", "", false);
            Profile.registry.addRawMacro(macro);
            addMacro.accept(macro);
        }));
    }

    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        fill(matricies, x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(matricies, x, y + height - 2, x + width, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x, y + height - 1, x + width, y + height, 0xFF7F7F7F);
        fill(matricies, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        int w = this.width - 12;
        
        drawCenteredString(matricies, this.textRenderer, textRenderer.trimToWidth(enabled, w/12), x + (w / 24), y + 2, 0xFFFFFF);
        fill(matricies, x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
        drawCenteredString(matricies, this.textRenderer, type, x + (w / 6), y + 2, 0xFFFFFF);
        fill(matricies, x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
        drawCenteredString(matricies, this.textRenderer, file, x + (w * 15 / 24), y + 2, 0xFFFFFF);
        fill(matricies, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    }
}
