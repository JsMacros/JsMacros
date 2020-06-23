package xyz.wagyourtail.jsmacros.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

public class MacroTypeWidget extends AlwaysSelectedEntryListWidget<MacroTypeWidget.MacroTypeEntry> {
    protected final MinecraftClient client;
    private final Text title = new TranslatableText("jsmacros.macrotypestitle");
    private final MacroEditScreen screen;
    public final List<MacroTypeWidget.MacroTypeEntry> macroTypes = Lists.newArrayList();
    
    public MacroTypeWidget(MinecraftClient client, MacroEditScreen screen, int width, int height) {
        super(client, width, height, 32, height - 55 + 4, 36);
        this.client = client;
        this.centerListVertically = false;
        this.screen = screen;
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
        this.updateEntries();
     }
    
    public void updateEntries() {
        this.macroTypes.clear();
        this.clearEntries();
        for (MacroEnum e : MacroEnum.values()) {
            if (e != MacroEnum.EVENT) {
                this.addMacroType(e.toString());
            }
        }
        for (String e : Profile.registry.events) {
            this.addMacroType(e);
        }
        this.macroTypes.forEach(this::addEntry);
    }
    
    public void addMacroType(String type) {
        this.macroTypes.add(new MacroTypeWidget.MacroTypeEntry(screen, type));
    }
    
    protected void renderHeader(MatrixStack matricies, int i, int j, Tessellator tessellator) {
        Text text = (new LiteralText("")).append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        this.client.textRenderer.draw(matricies, text, (float)(this.left + this.width / 2 - this.client.textRenderer.getWidth(text) / 2), (float)Math.min(this.top + 3, j), 16777215);
     }
    
    public boolean keyPressed(int key, int scancode, int mods) {
        MacroTypeEntry entry = (MacroTypeEntry)this.getSelected();
        return entry != null && entry.keyPressed(key, scancode, mods) || super.keyPressed(key, scancode, mods);
    }
    
    public void setSelected(MacroTypeWidget.MacroTypeEntry entry) {
        super.setSelected(entry);
    }
    
    protected boolean isMoveable() {
        return false;
    }
    
    public int getRowWidth() {
        return this.width;
     }
    
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }
    
    protected int getScrollbarPositionX() {
        return this.right - 6;
     }
    
    public static class MacroTypeEntry extends AlwaysSelectedEntryListWidget.Entry<MacroTypeWidget.MacroTypeEntry> {
        protected final MinecraftClient client;
        public final String macroType;
        private final MacroEditScreen screen;
        private static final Identifier key_down_tex = new Identifier(jsMacros.MOD_ID, "resources/key_down.png");
        private static final Identifier key_up_tex = new Identifier(jsMacros.MOD_ID, "resources/key_up.png");
        private static final Identifier key_both_tex = new Identifier(jsMacros.MOD_ID, "resources/key_both.png");
        private static final Identifier event_tex = new Identifier(jsMacros.MOD_ID, "resources/event.png");
        
        public MacroTypeEntry(MacroEditScreen screen, String macroType) {
            this.screen = screen;
            this.client = MinecraftClient.getInstance();
            this.macroType = macroType;
        }
        
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.screen.select(this);
            return true;
        }
        
        public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
            switch(macroType) {
                case "KEY_FALLING":
                    this.client.textRenderer.draw(matrices, "KEY UP", (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
                    this.draw(matrices, x, y, key_up_tex);
                    break;
                case "KEY_RISING":
                    this.client.textRenderer.draw(matrices, "KEY DOWN", (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
                    this.draw(matrices, x, y, key_down_tex);
                    break;
                case "KEY_BOTH":
                    this.client.textRenderer.draw(matrices, "KEY UP/DOWN", (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
                    this.draw(matrices, x, y, key_both_tex);
                    break;
                default:
                    this.client.textRenderer.draw(matrices, macroType, (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
                    this.draw(matrices, x, y, event_tex);
            }
        }
        
        protected void draw(MatrixStack matrices, int x, int y, Identifier textureId) {
            this.client.getTextureManager().bindTexture(textureId);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 32, 32, 0, 0, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }
        
        public boolean keyPressed(int keyCode, int scanCode, int mods) {
            return super.keyPressed(keyCode, scanCode, mods);
        }

    }
}