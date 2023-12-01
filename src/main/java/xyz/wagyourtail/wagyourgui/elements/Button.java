package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class Button extends PressableWidget {
    protected final TextRenderer textRenderer;
    protected int color;
    protected int borderColor;
    protected int highlightColor;
    protected int textColor;
    protected List<OrderedText> textLines;
    protected int visibleLines;
    protected int verticalCenter;
    public boolean horizCenter = true;
    public Consumer<Button> onPress;
    public boolean hovering = false;
    public boolean forceHover = false;

    public Button(int x, int y, int width, int height, TextRenderer textRenderer, int color, int borderColor, int highlightColor, int textColor, Text message, Consumer<Button> onPress) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.color = color;
        this.borderColor = borderColor;
        this.highlightColor = highlightColor;
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

    protected void setMessageSuper(Text message) {
        super.setMessage(message);
    }

    @Override
    public void setMessage(Text message) {
        super.setMessage(message);
        this.textLines = textRenderer.wrapLines(message, width - 4);
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.fontHeight, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.fontHeight)) / 2;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setHighlightColor(int color) {
        this.highlightColor = color;
    }

    protected void renderMessage(MatrixStack drawContext) {
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getWidth(textLines.get(i));
            textRenderer.draw(drawContext, textLines.get(i), (int) (horizCenter ? x + width / 2F - w / 2F : x + 1), y + 2 + verticalCenter + (i * textRenderer.fontHeight), textColor);
        }
    }

    @Override
    public void render(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active || forceHover) {
                hovering = true;
                fill(drawContext, x + 1, y + 1, x + width - 1, y + height - 1, highlightColor);
            } else {
                hovering = false;
                fill(drawContext, x + 1, y + 1, x + width - 1, y + height - 1, color);
            }
            // outline
            fill(drawContext, x, y, x + 1, y + height, borderColor);
            fill(drawContext, x + width - 1, y, x + width, y + height, borderColor);
            fill(drawContext, x + 1, y, x + width - 1, y + 1, borderColor);
            fill(drawContext, x + 1, y + height - 1, x + width - 1, y + height, borderColor);
            this.renderMessage(drawContext);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        //super.onClick(mouseX, mouseY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (this.active)
            super.onClick(mouseX, mouseY);
    }

    @Override
    public void onPress() {
        if (onPress != null) {
            onPress.accept(this);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

}
