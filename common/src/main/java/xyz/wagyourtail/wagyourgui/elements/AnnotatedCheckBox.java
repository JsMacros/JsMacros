package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnnotatedCheckBox extends Button {
    public boolean value;
    
    public AnnotatedCheckBox(int x, int y, int width, int height, FontRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, IChatComponent message, boolean initialValue, Consumer<Button> onPress) {
        super(x, y, width, height, textRenderer, color, borderColor, hilightColor, textColor, message, onPress);
        value = initialValue;
        horizCenter = false;
    }
    
    @Override
    public void onPress() {
        value = !value;
        super.onPress();
    }
    
    @Override
    public void setMessage(IChatComponent message) {
        setMessageSuper(message);
        int width = this.width - height;
        this.textLines = textRenderer.wrapLines(message.asFormattedString(), width - 4).stream().map(ChatComponentText::new).collect(Collectors.toList());
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.fontHeight, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.fontHeight)) / 2;
    }
    
    @Override
    protected void renderMessage() {
        int width = this.width - height;
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getStringWidth(textLines.get(i).asFormattedString());
            textRenderer.draw(textLines.get(i).asFormattedString(),
                horizCenter ? (int) (x + width / 2F - w / 2F) : x + 1, y + 2 + verticalCenter + (i * textRenderer.fontHeight), textColor);
        }
    }
    
    @Override
    public void render(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
        this.renderMessage();
        
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active || forceHover) {
                hovering = true;
                fill(x + width - height + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            } else {
                hovering = false;
                if (value) {
                fill(x + width - height + 1, y + 1, x + width - 1, y + height - 1, color);
                }
            }
            // outline
            fill(x + width - height, y, x + width - height + 1, y + height, borderColor);
            fill(x + width - 1, y, x + width, y + height, borderColor);
            fill(x + width - height + 1, y, x + width - 1, y + 1, borderColor);
            fill(x  + width - height + 1, y + height - 1, x + width - 1, y + height, borderColor);
        }
    }
}
