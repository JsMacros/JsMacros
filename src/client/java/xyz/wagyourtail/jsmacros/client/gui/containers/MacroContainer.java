package xyz.wagyourtail.jsmacros.client.gui.containers;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.util.TranslationUtil;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;

import java.io.File;
import java.util.List;

public class MacroContainer extends MultiElementContainer<MacroScreen> {
    private static final Identifier key_down_tex = Identifier.of(JsMacros.MOD_ID, "resources/key_down.png");
    private static final Identifier key_up_tex = Identifier.of(JsMacros.MOD_ID, "resources/key_up.png");
    private static final Identifier key_both_tex = Identifier.of(JsMacros.MOD_ID, "resources/key_both.png");
    @SuppressWarnings("unused")
    private static final Identifier event_tex = Identifier.of(JsMacros.MOD_ID, "resources/event.png");
    private static final Identifier script_fork_tex = Identifier.of(JsMacros.MOD_ID, "resources/script_fork.png");
    private static final Identifier script_join_tex = Identifier.of(JsMacros.MOD_ID, "resources/script_join.png");
    private final MinecraftClient mc;
    private final ScriptTrigger macro;
    private Button enableBtn;
    private Button keyBtn;
    private Button fileBtn;
    private Button delBtn;
    private Button editBtn;
    private Button keyStateBtn;
    private Button joinedBtn;
    private boolean selectkey = false;

    public MacroContainer(int x, int y, int width, int height, TextRenderer textRenderer, ScriptTrigger macro, MacroScreen parent) {
        super(x, y, width, height, textRenderer, parent);
        this.macro = macro;
        this.mc = MinecraftClient.getInstance();
        init();
    }

    public ScriptTrigger getRawMacro() {
        return macro;
    }

    @Override
    public void init() {
        super.init();
        int w = width - 12;
        enableBtn = addDrawableChild(new Button(x + 1, y + 1, w / 12 - 1, height - 2, textRenderer, macro.enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.translatable(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"), (btn) -> {
            macro.enabled = !macro.enabled;
            btn.setColor(macro.enabled ? 0x7000FF00 : 0x70FF0000);
            btn.setMessage(Text.translatable(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"));
        }));

        keyBtn = addDrawableChild(new Button(x + w / 12 + 1, y + 1, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? (w / 4) - (w / 12) - 1 - height : (w / 4) - (w / 12) - 1 - height * 2, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? TranslationUtil.getTranslatedEventName(macro.event) : buildKeyName(macro.event), (btn) -> {
            if (macro.triggerType == ScriptTrigger.TriggerType.EVENT) {
                parent.setEvent(this);
            } else {
                selectkey = true;
                btn.setMessage(Text.translatable("jsmacros.presskey"));
            }
        }));
        if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) {
            joinedBtn = addDrawableChild(new Button(x + w / 4 - height * 2, y + 1, height, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal(""), (btn) -> {
                macro.joined = !macro.joined;
            }));
            keyStateBtn = addDrawableChild(new Button(x + w / 4 - height, y + 1, height, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal(""), (btn) -> {
                switch (macro.triggerType) {
                    default:
                    case KEY_RISING:
                        macro.triggerType = ScriptTrigger.TriggerType.KEY_FALLING;
                        break;
                    case KEY_FALLING:
                        macro.triggerType = ScriptTrigger.TriggerType.KEY_BOTH;
                        break;
                    case KEY_BOTH:
                        macro.triggerType = ScriptTrigger.TriggerType.KEY_RISING;
                        break;
                }
            }));
        } else {
            joinedBtn = addDrawableChild(new Button(x + w / 4 - height, y + 1, height, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal(""), (btn) -> {
                macro.joined = !macro.joined;
            }));
        }


        final String fileName;
        if (macro.scriptFile.isAbsolute()) {
            fileName = JsMacrosClient.clientCore.config.macroFolder.toPath().relativize(macro.scriptFile).toString();
        } else {
            fileName = macro.scriptFile.toString();
        }
        fileBtn = addDrawableChild(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal("./" + fileName.replaceAll("\\\\", "/")), (btn) -> {
            parent.setFile(this);
        }));

        editBtn = addDrawableChild(new Button(x + w - 32, y + 1, 30, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.translatable("selectServer.edit"), (btn) -> {
            if (macro.scriptFile != null) {
                final File file;
                if (macro.scriptFile.isAbsolute()) {
                    file = macro.scriptFile.toFile();
                } else {
                    file = JsMacrosClient.clientCore.config.macroFolder.toPath().resolve(macro.scriptFile).toFile();
                }
                parent.editFile(file);
            }
        }));

        delBtn = addDrawableChild(new Button(x + w - 1, y + 1, 12, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal("X"), (btn) -> {
            parent.confirmRemoveMacro(this);
        }));
    }

    public void setEventType(String type) {
        BaseEventRegistry reg = JsMacrosClient.clientCore.eventRegistry;
        reg.removeScriptTrigger(macro);
        macro.event = type;
        reg.addScriptTrigger(macro);
        keyBtn.setMessage(TranslationUtil.getTranslatedEventName(macro.event));
    }

    public void setFile(File f) {
        macro.scriptFile = JsMacrosClient.clientCore.config.macroFolder.toPath().relativize(f.toPath());
        final String fileName = macro.scriptFile.toString();
        fileBtn.setMessage(Text.literal("./" + fileName.replaceAll("\\\\", "/")));
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        int w = width - 12;
        enableBtn.setPos(x + 1, y + 1, w / 12 - 1, height - 2);
        keyBtn.setPos(x + w / 12 + 1, y + 1, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? (w / 4) - (w / 12) - 1 - height : (w / 4) - (w / 12) - 1 - height * 2, height - 2);
        if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) {
            joinedBtn.setPos(x + w / 4 - height * 2, y + 1, height, height - 2);
            keyStateBtn.setPos(x + w / 4 - height, y + 1, height, height - 2);
        } else {
            joinedBtn.setPos(x + w / 4 - height, y + 1, height, height - 2);
        }
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
        MutableText text = Text.literal("");
        boolean notfirst = false;
        for (String s : translationKeys.split("\\+")) {
            if (notfirst) {
                text.append("+");
            }
            text.append(JsMacrosClient.getKeyText(s));
            notfirst = true;
        }
        return text;
    }

    public void setKey(String translationKeys) {
        JsMacrosClient.clientCore.eventRegistry.removeScriptTrigger(macro);
        macro.event = translationKeys;
        JsMacrosClient.clientCore.eventRegistry.addScriptTrigger(macro);
        keyBtn.setMessage(buildKeyName(translationKeys));
        selectkey = false;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        BaseEventRegistry reg = JsMacrosClient.clientCore.eventRegistry;
        if (macro.triggerType == ScriptTrigger.TriggerType.EVENT && reg.events.contains(macro.event)) {
            joinedBtn.active = reg.joinableEvents.contains(macro.event);
            if (!joinedBtn.active) {
                joinedBtn.setColor(0xA0000000);
                macro.joined = false;
            } else {
                joinedBtn.setColor(0);
            }
        } else {
            joinedBtn.setColor(0);
            joinedBtn.active = true;
        }
        if (visible) {
            int w = this.width - 12;
            // separate
            drawContext.fill(x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
            drawContext.fill(x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
            drawContext.fill(x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
            //RenderSystem.setShader(VertexFormats.POSITION_TEXTURE);
            // icon for keystate
            Identifier tex;
            if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) {
                if (macro.joined) {
                    tex = script_join_tex;
                } else {
                    tex = script_fork_tex;
                }
                GlStateManager._enableBlend();
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, tex, x + w / 4 - 2 * height + 2, y + 2, 0, 0, height - 4, height - 4, 32, 32, 32, 32);
                GlStateManager._disableBlend();
                switch (macro.triggerType) {
                    default:
                    case KEY_FALLING:
                        tex = key_up_tex;
                        break;
                    case KEY_RISING:
                        tex = key_down_tex;
                        break;
                    case KEY_BOTH:
                        tex = key_both_tex;
                        break;
                }
                GlStateManager._enableBlend();
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, tex, x + w / 4 - height + 2, y + 2, 0, 0, height - 4, height - 4, 32, 32, 32, 32);
                GlStateManager._disableBlend();
            } else {
                if (macro.joined) {
                    tex = script_join_tex;
                } else {
                    tex = script_fork_tex;
                }
                GlStateManager._enableBlend();
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, tex, x + w / 4 - height + 2, y + 2, 0, 0, height - 4, height - 4, 32, 32, 32, 32);
                GlStateManager._disableBlend();
            }

            // border
            drawContext.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
            drawContext.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            drawContext.fill(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            drawContext.fill(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);

            // overlay
            if (keyBtn.hovering && keyBtn.cantRenderAllText()) {
                drawContext.fill(mouseX - 2, mouseY - textRenderer.fontHeight - 3, mouseX + textRenderer.getWidth(keyBtn.getMessage()) + 2, mouseY, 0xFF000000);
                drawContext.drawTextWithShadow(textRenderer, keyBtn.getMessage(), mouseX, mouseY - textRenderer.fontHeight - 1, 0xFFFFFFFF);
            }
            if (fileBtn.hovering && fileBtn.cantRenderAllText()) {
                List<OrderedText> lines = textRenderer.wrapLines(fileBtn.getMessage(), this.x + this.width - mouseX);
                int top = mouseY - (textRenderer.fontHeight * lines.size()) - 2;
                int width = lines.stream().map(e -> textRenderer.getWidth(e)).reduce(0, Math::max);
                drawContext.fill(mouseX - 2, top - 1, mouseX + width + 2, mouseY, 0xFF000000);
                for (int i = 0; i < lines.size(); ++i) {
                    int wi = textRenderer.getWidth(lines.get(i)) / 2;
                    drawContext.drawText(textRenderer, lines.get(i), mouseX + width / 2 - wi, top + textRenderer.fontHeight * i, 0xFFFFFFFF, false);
                }
            }
        }
    }

}
