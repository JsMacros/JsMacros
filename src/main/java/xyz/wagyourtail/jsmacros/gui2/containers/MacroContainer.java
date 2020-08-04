package xyz.wagyourtail.jsmacros.gui2.containers;

import java.io.File;
import java.util.List;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

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
    private Consumer<MacroContainer> setEvent;

    public MacroContainer(int x, int y, int width, int height, TextRenderer textRenderer, RawMacro macro, Consumer<AbstractButtonWidget> addButton, Consumer<MacroContainer> onRemove, Consumer<MacroContainer> openFile, Consumer<MacroContainer>setEvent) {
        super(x, y, width, height, textRenderer, addButton);
        this.macro = macro;
        this.onRemove = onRemove;
        this.openFile = openFile;
        this.setEvent = setEvent;
        this.mc = MinecraftClient.getInstance();
        init();
    }
    
    public RawMacro getRawMacro() {
        return macro;
    }
    
    public void init() {
        super.init();
        int w = width - 12;
        enableBtn = (Button) addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 2, macro.enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"), (btn) -> {
            Profile.registry.removeRawMacro(macro);
            macro.enabled = !macro.enabled;
            Profile.registry.addRawMacro(macro);
            btn.setColor(macro.enabled ? 0x7000FF00 : 0x70FF0000);
            btn.setMessage(new TranslatableText(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"));
        }));

        keyBtn = (Button) addButton(new Button(x + w / 12 + 1, y + 1, macro.type == MacroEnum.EVENT ? (w / 4) - (w / 12) - 1 : (w / 4) - (w / 12) - 1 - height, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, macro.type == MacroEnum.EVENT ? new LiteralText(macro.eventkey) : buildKeyName(macro.eventkey), (btn) -> {
            if (macro.type == MacroEnum.EVENT) {
                if (setEvent != null) setEvent.accept(this);
                //int l = Profile.registry.events.size();
                //setEventType(Profile.registry.events.get((Profile.registry.events.indexOf(macro.eventkey) + 1) % l));
            } else {
                selectkey = true;
                btn.setMessage(new TranslatableText("jsmacros.presskey"));
            }
        }));
        if (macro.type != MacroEnum.EVENT) keyStateBtn = (Button) addButton(new Button(x + w / 4 - height, y + 1, height, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText(""), (btn) -> {
            Profile.registry.removeRawMacro(macro);
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
            Profile.registry.addRawMacro(macro);
        }));

        fileBtn = (Button) addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("./"+macro.scriptFile.replaceAll("\\\\", "/")), (btn) -> {
            if (openFile != null) openFile.accept(this);
        }));
        
        editBtn = (Button) addButton(new Button(x + w - 32, y + 1, 30, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("selectServer.edit"), (btn) -> {
            Util.getOperatingSystem().open(new File(jsMacros.config.macroFolder, macro.scriptFile));
        }));

        delBtn = (Button) addButton(new Button(x + w - 1, y + 1, 12, height - 2, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("X"), (btn) -> {
            if (onRemove != null) onRemove.accept(this);
        }));
    }
    
    public void setEventType(String type) {
        Profile.registry.removeRawMacro(macro);
        macro.eventkey = type;
        Profile.registry.addRawMacro(macro);
        keyBtn.setMessage(new LiteralText(macro.eventkey));
    }
    
    public void setFile(File f) {
        Profile.registry.removeRawMacro(macro);
        macro.scriptFile = f.getAbsolutePath().substring(jsMacros.config.macroFolder.getAbsolutePath().length()+1);
        Profile.registry.addRawMacro(macro);
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
    
    public boolean onKey(String translationKey) {
        if (selectkey) {
            setKey(translationKey);
            return false;
        }
        return true;
    }
    
    public static Text buildKeyName(String translationKeys) {
        LiteralText text = new LiteralText("");
        boolean notfirst = false;
        for (String s : translationKeys.split("\\+")) {
            if (notfirst) text.append("+");
            text.append(jsMacros.getKeyText(s));
            notfirst = true;
        }
        return text;
    }
    
    public void setKey(String translationKeys) {
        Profile.registry.removeRawMacro(macro);
        macro.eventkey = translationKeys;
        Profile.registry.addRawMacro(macro);
        keyBtn.setMessage(buildKeyName(translationKeys));
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
            
            // overlay
            if (keyBtn.hovering && !keyBtn.canRenderAllText()) {
                fill(matricies, mouseX-2, mouseY-textRenderer.fontHeight - 3, mouseX+textRenderer.getWidth(keyBtn.getMessage())+2, mouseY, 0xFF000000);
                drawTextWithShadow(matricies, textRenderer, keyBtn.getMessage(), mouseX, mouseY-textRenderer.fontHeight - 1, 0xFFFFFF);
            }
            if (fileBtn.hovering && !fileBtn.canRenderAllText()) {
                List<StringRenderable> lines = textRenderer.wrapLines(fileBtn.getMessage(), this.x + this.width - mouseX);
                int top = mouseY-(textRenderer.fontHeight*lines.size())-2;
                int width = lines.stream().map(e -> textRenderer.getWidth(e)).reduce(0, (e, t) -> Math.max(e, t));
                fill(matricies, mouseX-2, top - 1, mouseX+width+2, mouseY, 0xFF000000);
                for (int i = 0; i < lines.size(); ++i)
                    this.drawCenteredText(matricies, textRenderer, lines.get(i), mouseX + width/2, top+textRenderer.fontHeight*i, 0xFFFFFF);
            }
        }
    }
}
