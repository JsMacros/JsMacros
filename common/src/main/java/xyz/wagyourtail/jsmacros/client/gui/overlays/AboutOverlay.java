package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.util.List;
import java.util.stream.Collectors;

public class AboutOverlay extends OverlayContainer {
    private List<IChatComponent> text;
    private int lines;
    private int vcenter;

    public AboutOverlay(int x, int y, int width, int height, FontRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer,0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("X"), (btn) -> this.close()));

        this.addButton(new Button(x + 2, y + height - 14, w / 3, 12, textRenderer,0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("Website"), (btn) -> JsMacros.openURI("https://jsmacros.wagyourtail.xyz")));

        this.addButton(new Button(x + w / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("Discord"), (btn) -> JsMacros.openURI("https://discord.gg/P6W58J8")));


        this.addButton(new Button(x + w * 2 / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("CurseForge"), (btn) -> JsMacros.openURI("https://www.curseforge.com/minecraft/mc-mods/jsmacros")));

        this.setMessage(new ChatComponentTranslation("jsmacros.aboutinfo"));
    }
    
    public void setMessage(IChatComponent message) {
        this.text = textRenderer.wrapLines(message.asFormattedString(), width - 6).stream().map(
            ChatComponentText::new).collect(Collectors.toList());
        this.lines = Math.min(Math.max((height - 27) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 12) - (lines * textRenderer.fontHeight)) / 2;
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getStringWidth(text.get(i).asFormattedString());
            textRenderer.draw(text.get(i).asFormattedString(),
                (int) (x + width / 2F - w / 2F), y + 2 + vcenter + (i * textRenderer.fontHeight), 0xFFFFFF);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        
        textRenderer.drawTrimmed(new ChatComponentTranslation("jsmacros.about").asFormattedString(), x + 3, y + 3, width - 14, 0xFFFFFF);
        renderMessage();
        
        fill(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
        super.render(mouseX, mouseY, delta);
        
    }
}
