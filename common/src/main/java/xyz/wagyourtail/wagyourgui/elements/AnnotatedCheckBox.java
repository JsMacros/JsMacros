package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class AnnotatedCheckBox extends Button {
    public boolean value;
    
    public AnnotatedCheckBox(int x, int y, int width, int height, TextRenderer textRenderer, int color, int borderColor, int highlightColor, int textColor, Text message, boolean initialValue, Consumer<Button> onPress) {
        super(x, y, width, height, textRenderer, color, borderColor, highlightColor, textColor, message, onPress);
        value = initialValue;
        horizCenter = false;
    }
    
    @Override
    public void onPress() {
        value = !value;
        super.onPress();
    }
    
    @Override
    public void setMessage(Text message) {
        setMessageSuper(message);
        int width = this.width - height;
        this.textLines = textRenderer.wrapLines(message, width - 4);
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.fontHeight, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.fontHeight)) / 2;
    }
    
    @Override
    protected void renderMessage(MatrixStack matrices) {
        int width = this.width - height;
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getWidth(textLines.get(i));
            textRenderer.draw(matrices, textLines.get(i), horizCenter ? getX() + width / 2F - w / 2F : getX() + 1, getY() + 2 + verticalCenter + (i * textRenderer.fontHeight), textColor);
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.renderMessage(matrices);
        
            // fill
            if (mouseX - getX() >= 0 && mouseX - getX() - width <= 0 && mouseY - getY() >= 0 && mouseY - getY() - height <= 0 && this.active || forceHover) {
                hovering = true;
                fill(matrices, getX() + width - height + 1, getY() + 1, getX() + width - 1, getY() + height - 1, highlightColor);
            } else {
                hovering = false;
                if (value) {
                    fill(matrices, getX() + width - height + 1, getY() + 1, getX() + width - 1, getY() + height - 1, color);
                }
            }
            // outline
            fill(matrices, getX() + width - height, getY(), getX() + width - height + 1, getY() + height, borderColor);
            fill(matrices, getX() + width - 1, getY(), getX() + width, getY() + height, borderColor);
            fill(matrices, getX() + width - height + 1, getY(), getX() + width - 1, getY() + 1, borderColor);
            fill(matrices, getX() + width - height + 1, getY() + height - 1, getX() + width - 1, getY() + height, borderColor);
        }
    }
}
