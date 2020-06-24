package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.function.Consumer;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class MacroContainer extends MultiElementContainer {
    private RawMacro macro;
    private Button enableBtn;
    private Button keyBtn;
    private Button fileBtn;
    private Button delBtn;
    private boolean selectkey = false;
    private Consumer<MacroContainer> onRemove;

    public MacroContainer(int x, int y, int width, int height, TextRenderer textRenderer, RawMacro macro,  Consumer<AbstractButtonWidget> addButton, Consumer<MacroContainer> onRemove) {
        super(x, y, width, height, textRenderer, addButton);
        this.macro = macro;
        this.onRemove = onRemove;
        init();
    }
    
    public void init() {
        super.init();
        int w = width - 12;
        enableBtn = (Button) addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 2, macro.enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText(macro.enabled ? "Enabled" : "Disabled"), (btn) -> {
            Profile.registry.removeMacro(macro);
            macro.enabled = !macro.enabled;
            Profile.registry.addMacro(macro);
            btn.setColor(macro.enabled ? 0x7000FF00 : 0x70FF0000);
            btn.setMessage(new LiteralText(macro.enabled ? "Enabled" : "Disabled"));
        }));

        keyBtn = (Button) addButton(new Button(x + w / 12 + 1, y + 1, (w / 4) - (w / 12) - 1, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, macro.type == MacroEnum.EVENT ? new LiteralText(macro.eventkey) : jsMacros.getKeyText(macro.eventkey), (btn) -> {
            selectkey = true;
            btn.setMessage(new LiteralText("- Press A Key -"));
        }));

        fileBtn = (Button) addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText(macro.scriptFile), (btn) -> {
            // TODO
        }));

        delBtn = (Button) addButton(new Button(x + w - 1, y + 1, 12, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("X"), (btn) -> {
            if (onRemove != null) onRemove.accept(this);
        }));
    }

    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        int w = width - 12;
        enableBtn.setPos(x + 1, y + 1, w / 12 - 1, height - 2);
        keyBtn.setPos(x + w / 12 + 1, y + 1, (w / 4) - (w / 12) - 1, height - 2);
        fileBtn.setPos(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3, height - 2);
        delBtn.setPos(x + w - 1, y + 1, 12, height - 2);

    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectkey) {
            setKey(InputUtil.Type.KEYSYM.createFromCode(keyCode).getTranslationKey());
            return false;
        }
        return true;
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectkey) {
            setKey(InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey());
            return false;
        }
        return true;
    }
    
    public void setKey(String translationKey) {
        Profile.registry.removeMacro(macro);
        macro.eventkey = translationKey;
        Profile.registry.addMacro(macro);
        keyBtn.setMessage(jsMacros.getKeyText(translationKey));
        selectkey = false;
    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (visible) {
            int w = this.width - 12;
            // separate
            fill(matricies, x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
            fill(matricies, x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
            fill(matricies, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    
            // border
            fill(matricies, x, y, x + width, y + 1, 0xFFFFFFFF);
            fill(matricies, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            fill(matricies, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        }
    }

}
