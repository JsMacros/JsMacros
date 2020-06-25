package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

public class Button extends AbstractPressableButtonWidget {
    protected int color;
    protected int borderColor;
    protected int hilightColor;
    private int textColor;
    private ArrayList<StringRenderable> text;
    private MinecraftClient mc;
    private int lines;
    private int vcenter;
    public Consumer<Button> onPress;
    
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
    
    public void setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void setMessage(Text message) {
        super.setMessage(message);
        this.text = new ArrayList<>(this.mc.textRenderer.wrapLines(message, width - 4));
        this.lines = Math.min(height / mc.textRenderer.fontHeight, text.size());
        this.vcenter = ((height - 4) - (lines * mc.textRenderer.fontHeight)) / 2;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public void setHilightColor(int color) {
        this.hilightColor = color;
    }

    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active)
                fill(matricies, x + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            else
                fill(matricies, x + 1, y + 1, x + width - 1, y + height - 1, color);
            
            // outline
            fill(matricies, x, y, x + 1, y + height, borderColor);
            fill(matricies, x + width - 1, y, x + width, y + height, borderColor);
            fill(matricies, x + 1, y, x + width - 1, y + 1, borderColor);
            fill(matricies, x + 1, y + height - 1, x + width - 1, y + height, borderColor);
    
            for (int i = 0; i < lines; ++i) {
                drawCenteredText(matricies, mc.textRenderer, text.get(i), x + width / 2, y + 2 + vcenter + (i * mc.textRenderer.fontHeight), textColor);
            }
        }
    }
    

    @Override
    public void onPress() {
        if (onPress != null) onPress.accept(this);
    }

}
