package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Button extends GuiButton {
    protected final FontRenderer textRenderer;
    protected int color;
    protected int borderColor;
    protected int hilightColor;
    protected int textColor;
    protected List<IChatComponent> textLines;
    protected int visibleLines;
    protected int verticalCenter;
    public boolean horizCenter = true;
    public Consumer<Button> onPress;
    public boolean hovering = false;
    public boolean forceHover = false;

    public Button(int x, int y, int width, int height, FontRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, IChatComponent message, Consumer<Button> onPress) {
        super(1, x, y, width, height, message.asFormattedString());
        this.textRenderer = textRenderer;
        this.color = color;
        this.borderColor = borderColor;
        this.hilightColor = hilightColor;
        this.textColor = textColor;
        this.onPress = onPress;
        this.setMessage(message);
    }

    public Button setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public boolean cantRenderAllText() {
        return this.textLines.size() > this.visibleLines;
    }

    protected void setMessageSuper(IChatComponent message) {
        this.message = message.asFormattedString();
    }

    public void setMessage(IChatComponent message) {
        this.message = message.asFormattedString();
        this.textLines = textRenderer.wrapLines(message.asFormattedString(), width - 4).stream().map(
            ChatComponentText::new).collect(Collectors.toList());
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.fontHeight, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.fontHeight)) / 2;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setHilightColor(int color) {
        this.hilightColor = color;
    }

    protected void renderMessage() {
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getStringWidth(textLines.get(i).asFormattedString());
            textRenderer.drawWithShadow(textLines.get(i).asFormattedString(), horizCenter ? (int) (x + width / 2F - w / 2F) : x + 1, y + 2 + verticalCenter + (i * textRenderer.fontHeight), textColor);
        }
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active || forceHover) {
                hovering = true;
                fill(x + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            } else {
                hovering = false;
                fill(x + 1, y + 1, x + width - 1, y + height - 1, color);
            }
            // outline
            fill(x, y, x + 1, y + height, borderColor);
            fill(x + width - 1, y, x + width, y + height, borderColor);
            fill(x + 1, y, x + width - 1, y + 1, borderColor);
            fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor);
            this.renderMessage();
        }
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY)
    {
        if(this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            onPress();
        }
    }

    public void onPress() {
        if (onPress != null) onPress.accept(this);
    }

}