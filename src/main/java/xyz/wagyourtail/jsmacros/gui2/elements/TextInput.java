package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.function.Consumer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class TextInput extends Button {
    public Consumer<String> onChange;
    public String content;
    public int selStart;
    public int selEnd;
    
    public TextInput(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, String message, Consumer<String> onChange) {
        super(x, y, width, height, color, borderColor, hilightColor, textColor, new LiteralText(message), null);
        this.onChange = onChange;
    }
    
    public void setMessage(String message) {
        super.setMessage(new LiteralText(message));
    }
    
    protected void renderMessage(MatrixStack matricies) {
        drawTextWithShadow(matricies, mc.textRenderer, text.get(0), x + 2, y + 2, textColor);
    }
}
