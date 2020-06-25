package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.HashMap;
import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public class OverlayContainer extends MultiElementContainer {
    public HashMap<AbstractButtonWidget, Boolean> savedBtnStates;
    
    public OverlayContainer(int x, int y, int width, int height, TextRenderer textRenderer, Consumer<AbstractButtonWidget> addButton) {
        super(x, y, width, height, textRenderer, addButton);
    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        for (AbstractButtonWidget btn : buttons) {
            btn.render(matricies, mouseX, mouseY, delta);
        }
    }

}
