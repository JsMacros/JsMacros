package xyz.wagyourtail.jsmacros.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class MacroListWidget extends AlwaysSelectedEntryListWidget<MacroListWidget.MacroEntry> {
    private final MacroListScreen screen;
    private final List<MacroListWidget.MacroEntry> macros = Lists.newArrayList();
    
    public MacroListWidget(MacroListScreen screen, MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
        super(minecraftClient, width, height, top, bottom,entryHeight);
        this.screen = screen;
        this.updateEntries();
    }
    
    public void updateEntries() {
        this.macros.clear();
        this.clearEntries();
        jsMacros.profile.toRawProfile().forEach(this::addMacro);
        this.macros.forEach(this::addEntry);
    }
    
    public void addMacro(RawMacro macro) {
        this.macros.add(new MacroListWidget.MacroEntry(screen, macro));
    }
    
    public boolean keyPressed(int key, int scancode, int mods) {
        MacroEntry entry = (MacroEntry)this.getSelected();
        return entry != null && entry.keyPressed(key, scancode, mods) || super.keyPressed(key, scancode, mods);
    }
    
    public void setSelected(MacroListWidget.MacroEntry entry) {
        super.setSelected(entry);
    }
    
    protected boolean isMoveable() {
        return false;
    }
    
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }
    
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }
    
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }
    
    public static class MacroEntry extends AlwaysSelectedEntryListWidget.Entry<MacroListWidget.MacroEntry> {
        private final MacroListScreen screen;
        private final MinecraftClient client;
        private final RawMacro macro;
        private static final Identifier key_down_tex = new Identifier(jsMacros.MOD_ID, "resources/key_down.png");
        private static final Identifier key_up_tex = new Identifier(jsMacros.MOD_ID, "resources/key_up.png");
        private static final Identifier key_both_tex = new Identifier(jsMacros.MOD_ID, "resources/key_both.png");
        private static final Identifier event_tex = new Identifier(jsMacros.MOD_ID, "resources/event.png");
        
        protected MacroEntry(MacroListScreen screen, RawMacro macro) {
            this.screen = screen;
            this.client = jsMacros.getMinecraft();
            this.macro = macro;
        }
        
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
            this.client.textRenderer.draw(macro.type == MacroEnum.EVENT ? macro.eventkey : jsMacros.getLocalizedName(InputUtil.fromName(macro.eventkey)), (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
            
            List<String> list = this.client.textRenderer.wrapStringToWidthAsList(macro.scriptFileName(), width - 32 - 2);
            for (int n = 0; n < Math.min(list.size(), 2); ++n) {
                TextRenderer renderer = this.client.textRenderer;
                String stringPart = (String)list.get(n);
                float xPosition = (float)(x + 32 + 3);
                float yPosition = (float)(y + 12);
                renderer.draw(stringPart, xPosition, yPosition + 9 * n, 0x808080);
            }
            
            switch(macro.type) {
                case KEY_FALLING:
                    this.draw(x, y, key_up_tex);
                    break;
                case KEY_RISING:
                    this.draw(x, y, key_down_tex);
                    break;
                case KEY_BOTH:
                    this.draw(x, y, key_both_tex);
                    break;
                case EVENT:
                    this.draw(x, y, event_tex);
                    break;
            }
        }

        protected void draw(int x, int y, Identifier textureId) {
            this.client.getTextureManager().bindTexture(textureId);
            RenderSystem.enableBlend();
            DrawableHelper.blit(x, y, 0.0F, 0.0F, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }
        
        public boolean keyPressed(int keyCode, int scanCode, int mods) {
            return super.keyPressed(keyCode, scanCode, mods);
        }
        
        public RawMacro getRawMacro() {
            return macro;
        }
        
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.screen.select(this);
            return true;
        }
    }
    
    
}