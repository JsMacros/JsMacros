package xyz.wagyourtail.jsmacros.gui2.containers;

import java.io.File;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MultiElementContainer;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MacroContainer extends MultiElementContainer {
    private static final Identifier key_down_tex = new Identifier(jsMacros.MOD_ID, "resources/key_down.png");
    private static final Identifier key_up_tex = new Identifier(jsMacros.MOD_ID, "resources/key_up.png");
    private static final Identifier key_both_tex = new Identifier(jsMacros.MOD_ID, "resources/key_both.png");
    @SuppressWarnings("unused")
    private static final Identifier event_tex = new Identifier(jsMacros.MOD_ID, "resources/event.png");
    private MinecraftClient mc;
    private RawMacro macro;
    private Button enableBtn;
    private Button keyBtn;
    private Button fileBtn;
    private Button delBtn;
    private Button editBtn;
    private Button keyStateBtn;
    private boolean selectkey = false;
    private Consumer<MacroContainer> onRemove;
    private Consumer<MacroContainer> openFile;

    public MacroContainer(int x, int y, int width, int height, TextRenderer textRenderer, RawMacro macro,  Consumer<AbstractButtonWidget> addButton, Consumer<MacroContainer> onRemove, Consumer<MacroContainer> openFile) {
        super(x, y, width, height, textRenderer, addButton);
        this.macro = macro;
        this.onRemove = onRemove;
        this.openFile = openFile;
        this.mc = MinecraftClient.getInstance();
        init();
    }
    
    public RawMacro getRawMacro() {
        return macro;
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

        keyBtn = (Button) addButton(new Button(x + w / 12 + 1, y + 1, macro.type == MacroEnum.EVENT ? (w / 4) - (w / 12) - 1 : (w / 4) - (w / 12) - 1 - height, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, macro.type == MacroEnum.EVENT ? new LiteralText(macro.eventkey) : jsMacros.getKeyText(macro.eventkey), (btn) -> {
            if (macro.type == MacroEnum.EVENT) {
                Profile.registry.removeMacro(macro);
                int l = Profile.registry.events.size();
                macro.eventkey = Profile.registry.events.get((Profile.registry.events.indexOf(macro.eventkey) + 1) % l);
                Profile.registry.addMacro(macro);
                btn.setMessage(new LiteralText(macro.eventkey));
            } else {
                selectkey = true;
                btn.setMessage(new LiteralText("- Press A Key -"));
            }
        }));
        if (macro.type != MacroEnum.EVENT) keyStateBtn = (Button) addButton(new Button(x + w / 4 - height, y + 1, height, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText(""), (btn) -> {
            Profile.registry.removeMacro(macro);
            switch(macro.type) {
            default:
            case KEY_RISING:
                macro.type = MacroEnum.KEY_FALLING;
                break;
            case KEY_FALLING:
                macro.type = MacroEnum.KEY_BOTH;
                break;
            case KEY_BOTH:
                macro.type = MacroEnum.KEY_RISING;
                break;
            }
            Profile.registry.addMacro(macro);
        }));

        fileBtn = (Button) addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("./"+macro.scriptFile.replaceAll("\\\\", "/")), (btn) -> {
            if (openFile != null) openFile.accept(this);
        }));
        
        editBtn = (Button) addButton(new Button(x + w - 32, y + 1, 30, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("Edit"), null));

        delBtn = (Button) addButton(new Button(x + w - 1, y + 1, 12, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("X"), (btn) -> {
            Profile.registry.removeMacro(macro);
            if (onRemove != null) onRemove.accept(this);
        }));
    }
    
    public void setFile(File f) {
        Profile.registry.removeMacro(macro);
        macro.scriptFile = f.getAbsolutePath().substring(jsMacros.config.macroFolder.getAbsolutePath().length()+1);
        Profile.registry.addMacro(macro);
        fileBtn.setMessage(new LiteralText("./"+macro.scriptFile.replaceAll("\\\\", "/")));
    }

    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        int w = width - 12;
        enableBtn.setPos(x + 1, y + 1, w / 12 - 1, height - 2);
        keyBtn.setPos(x + w / 12 + 1, y + 1, macro.type == MacroEnum.EVENT ? (w / 4) - (w / 12) - 1 : (w / 4) - (w / 12) - 1 - height, height - 2);
        if (macro.type != MacroEnum.EVENT) keyStateBtn.setPos(x + w / 4 - height, y + 1, height, height - 2);
        fileBtn.setPos(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2);
        editBtn.setPos(x + w - 32, y + 1, 30, height - 2);
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
            
            // icon for keystate
            if (macro.type != MacroEnum.EVENT) {
                switch (macro.type) {
                default:
                case KEY_FALLING:
                    this.mc.getTextureManager().bindTexture(key_up_tex);
                    break;
                case KEY_RISING:
                    this.mc.getTextureManager().bindTexture(key_down_tex);
                    break;
                case KEY_BOTH:
                    this.mc.getTextureManager().bindTexture(key_both_tex);
                    break;
                }
                RenderSystem.enableBlend();
                drawTexture(matricies, x + w / 4 - height + 2, y + 2, height-4, height-4, 0, 0, 32, 32, 32, 32);
                RenderSystem.disableBlend();
            }
            
            // border
            fill(matricies, x, y, x + width, y + 1, 0xFFFFFFFF);
            fill(matricies, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            fill(matricies, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        }
    }

}
