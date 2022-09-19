package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class CheckBox extends CheckboxWidget {

    private final Consumer<CheckBox> action;

    public CheckBox(int x, int y, int width, int height, Text text, boolean checked, Consumer<CheckBox> action) {
        this(x, y, width, height, text, checked, true, action);
    }

    public CheckBox(int x, int y, int width, int height, Text text, boolean checked, boolean showMessage, Consumer<CheckBox> action) {
        super(x, y, width, height, text, checked, showMessage);
        this.action = action;
    }

    @Override
    public void onPress() {
        super.onPress();
        action.accept(this);
    }

}
