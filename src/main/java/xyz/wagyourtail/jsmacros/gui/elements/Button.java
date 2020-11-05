package xyz.wagyourtail.jsmacros.gui.elements;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.gui.screens.editor.EditorContent;

public class Button extends AbstractPressableButtonWidget {
    protected int color;
    protected int borderColor;
    protected int hilightColor;
    protected MinecraftClient mc;
    protected int textColor;
    protected List<OrderedText> text;
    protected int lines;
    protected int vcenter;
    public Consumer<Button> onPress;
    public boolean hovering = false;
    
    public Button(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, Text message, Consumer<Button> onPress) {
        super(x, y, width, height, message);
        this.color = color;
        this.borderColor = borderColor;
        this.hilightColor = hilightColor;
        this.textColor = textColor;
        this.mc = MinecraftClient.getInstance();
        this.setMessage(message);
        this.onPress = onPress;
    }
    
    public Button setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }
    
    public boolean canRenderAllText() {
        return this.text.size() <= this.lines;
    }
    
    public void setMessage(Text message) {
        super.setMessage(message);
        this.text = this.mc.textRenderer.wrapLines(message, width - 4);
        this.lines = Math.min(Math.max((height - 2) / mc.textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 4) - (lines * mc.textRenderer.fontHeight)) / 2;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public void setHilightColor(int color) {
        this.hilightColor = color;
    }
    
    protected void renderMessage(MatrixStack matricies) {
        for (int i = 0; i < lines; ++i) {
            int w = mc.textRenderer.getWidth(text.get(i));
            mc.textRenderer.draw(matricies, text.get(i), x + width / 2 - w / 2, y + 2 + vcenter + (i * mc.textRenderer.fontHeight), textColor);
        }
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active) {
                hovering = true;
                fill(matricies, x + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            } else {
                hovering = false;
                fill(matricies, x + 1, y + 1, x + width - 1, y + height - 1, color);
            }
            // outline
            fill(matricies, x, y, x + 1, y + height, borderColor);
            fill(matricies, x + width - 1, y, x + width, y + height, borderColor);
            fill(matricies, x + 1, y, x + width - 1, y + 1, borderColor);
            fill(matricies, x + 1, y + height - 1, x + width - 1, y + height, borderColor);
            this.renderMessage(matricies);
        }
    }
    
    public void onClick(double mouseX, double mouseY) {
        //super.onClick(mouseX, mouseY);
    }
    
    public void onRelease(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }
    
    @Override
    public void onPress() {
        if (onPress != null) onPress.accept(this);
    }

}
