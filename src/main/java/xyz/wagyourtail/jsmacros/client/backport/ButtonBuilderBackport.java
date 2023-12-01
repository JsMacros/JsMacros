package xyz.wagyourtail.jsmacros.client.backport;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ButtonBuilderBackport {

    int x;
    int y;
    int width;
    int height;

    Text text;
    ButtonWidget.PressAction action;

    public ButtonBuilderBackport(Text text, ButtonWidget.PressAction action) {
        this.text = text;
        this.action = action;
    }

    public static ButtonBuilderBackport builder(Text text, ButtonWidget.PressAction action) {
        return new ButtonBuilderBackport(text, action);
    }

    public ButtonBuilderBackport position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ButtonBuilderBackport size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ButtonWidget build() {
        return new ButtonWidget(x, y, width, height, text, action);
    }

}
