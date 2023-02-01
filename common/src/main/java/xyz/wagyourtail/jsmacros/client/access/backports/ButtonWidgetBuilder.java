package xyz.wagyourtail.jsmacros.client.access.backports;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ButtonWidgetBuilder {
    public static ButtonWidgetBuilder builder(Text text, ButtonWidget.PressAction pressAction) {
        return new ButtonWidgetBuilder(text, pressAction);
    }

    private final ButtonWidget.PressAction pressAction;
    private final Text text;
    private int x;
    private int y;
    private int width;
    private int height;

    private ButtonWidgetBuilder(Text text, ButtonWidget.PressAction pressAction) {
        this.text = text;
        this.pressAction = pressAction;
    }

    public ButtonWidgetBuilder position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ButtonWidgetBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ButtonWidget build() {
        return new ButtonWidget(x, y, width, height, text.asFormattedString(), pressAction);
    }
}
