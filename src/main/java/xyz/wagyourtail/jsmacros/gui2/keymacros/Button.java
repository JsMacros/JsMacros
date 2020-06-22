package xyz.wagyourtail.jsmacros.gui2.keymacros;

import java.util.ArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;

public class Button extends AbstractPressableButtonWidget {
    private int color;
    private int borderColor;
    private int hilightColor;
    private int textColor;
    private ArrayList<String> text;
    private MinecraftClient mc;
    private int lines;
    private int vcenter;

    public Button(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, String message) {
        super(x, y, width, height, message);
        this.color = color;
        this.borderColor = borderColor;
        this.hilightColor = hilightColor;
        this.textColor = textColor;
        this.mc = MinecraftClient.getInstance();
        this.setMessage(message);
    }

    public void setMessage(String message) {
        super.setMessage(message);
        this.text = new ArrayList<>(this.mc.textRenderer.wrapStringToWidthAsList(message, width - 4));
        this.lines = Math.min(height / mc.textRenderer.fontHeight, text.size());
        this.vcenter = ((width - 4) - (lines * mc.textRenderer.fontHeight)) / 2;
    }

    public void render(int mouseX, int mouseY, float delta) {
        // fill
        if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0) fill(x + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
        else fill(x + 1, y + 1, x + width - 1, y + height - 1, color);
        // outline
        fill(x, y, x + 1, y + height, borderColor);
        fill(x + width - 1, y, x + width, y + height, borderColor);
        fill(x + 1, y, x + width - 1, y + 1, borderColor);
        fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor);

        for (int i = 0; i < lines; ++i) {
            drawCenteredString(mc.textRenderer, text.get(i), x + width / 2, y + 2 + vcenter + (i * mc.textRenderer.fontHeight), textColor);
        }
    }

    @Override
    public void onPress() {
        // TODO Auto-generated method stub
        System.out.println("test");
    }

}
