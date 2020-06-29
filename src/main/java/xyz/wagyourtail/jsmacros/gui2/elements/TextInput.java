package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.function.Consumer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class TextInput extends Button {
    public Consumer<String> onChange;
    public String content;
    protected int selStart;
    public int selStartIndex;
    protected int selEnd;
    public int selEndIndex;
    
    public TextInput(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, String message, Consumer<String> onChange) {
        super(x, y, width, height, color, borderColor, hilightColor, textColor, new LiteralText(""), null);
        this.content = message;
        this.onChange = onChange;
        this.updateSelStart(0);
        this.updateSelEnd(0);
    }
    
    public void setMessage(String message) {
        content = message;
    }
    
    public void updateSelStart(int startIndex) {
        selStartIndex = startIndex;
        if (startIndex == 0) selStart = x + 2;
        else selStart = x + 2 + mc.textRenderer.getWidth(content.substring(0, startIndex));
    }
    
    public void updateSelEnd(int endIndex) {
        selEndIndex = endIndex;
        if (endIndex == 0) selEnd = x + 3;
        else selEnd = x + 3 + mc.textRenderer.getWidth(content.substring(0, endIndex));
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int pos = mc.textRenderer.trimToWidth(content, (int) (mouseX-x-2)).length();
        updateSelStart(pos);
        updateSelEnd(pos);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    protected void renderMessage(MatrixStack matricies) {
        fill(matricies, selStart, y+2, selEnd, y+3+mc.textRenderer.fontHeight, hilightColor);
        drawStringWithShadow(matricies, mc.textRenderer, content, x + 2, height > 9 ? y + 2 : y, this.isFocused() ? textColor : 0xFFFF0000);
    }
}
