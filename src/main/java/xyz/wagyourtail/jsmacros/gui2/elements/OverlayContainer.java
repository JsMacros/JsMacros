package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.HashMap;
import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public class OverlayContainer extends MultiElementContainer {
    public HashMap<AbstractButtonWidget, Boolean> savedBtnStates = new HashMap<>();
    public Consumer<OverlayContainer> close;
    
    public OverlayContainer(int x, int y, int width, int height, TextRenderer textRenderer, Consumer<AbstractButtonWidget> addButton, Consumer<OverlayContainer> close) {
        super(x, y, width, height, textRenderer, addButton);
        this.close = close;
    }
    
    public void close() {
        this.close.accept(this);
    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        for (AbstractButtonWidget btn : buttons) {
            btn.render(matricies, mouseX, mouseY, delta);
        }
    }

}
