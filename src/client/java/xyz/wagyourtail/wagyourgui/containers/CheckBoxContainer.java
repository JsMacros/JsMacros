package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import xyz.wagyourtail.wagyourgui.elements.Button;

import java.util.function.Consumer;

public class CheckBoxContainer extends MultiElementContainer<IContainerParent> {
    private boolean state;
    private Button checkBox;
    private final Consumer<Boolean> setState;
    public Text message;

    public CheckBoxContainer(int x, int y, int width, int height, TextRenderer textRenderer, boolean defaultState, Text message, IContainerParent parent, Consumer<Boolean> setState) {
        super(x, y, width, height, textRenderer, parent);
        this.state = defaultState;
        this.message = message;
        this.setState = setState;
        this.init();
    }

    @Override
    public void init() {
        super.init();

        checkBox = this.addDrawableChild(new Button(x, y, height, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal(state ? "\u2713" : ""), btn -> {
            state = !state;
            if (setState != null) {
                setState.accept(state);
            }
            btn.setMessage(Text.literal(state ? "\u2713" : ""));
        }));
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        checkBox.setPos(x + 1, y + 1, height - 2, height - 2);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            drawContext.drawWrappedText(textRenderer, message, x + height, y + 2, width - height - 2, 0xFFFFFFFF, false);
        }
    }

}
