package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.HashMap;
import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public class OverlayContainer extends MultiElementContainer {
    public HashMap<AbstractButtonWidget, Boolean> savedBtnStates = new HashMap<>();
    public Scrollbar scroll;
    public Consumer<OverlayContainer> close;
    Consumer<AbstractButtonWidget> removeButton;
    private OverlayContainer overlay;
    
    public OverlayContainer(int x, int y, int width, int height, TextRenderer textRenderer, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close) {
        super(x, y, width, height, textRenderer, addButton);
        this.removeButton = removeButton;
        this.close = close;
    }
    
    public void removeButton(AbstractButtonWidget btn) {
        this.buttons.remove(btn);
        if (removeButton != null) removeButton.accept(btn);
    }
    
    public void openOverlay(OverlayContainer overlay) {
        for (AbstractButtonWidget b : buttons) {
            overlay.savedBtnStates.put(b, b.active);
            b.active = false;
        }
        this.overlay = overlay;
        overlay.init();
    }
    
    public void closeOverlay(OverlayContainer overlay) {
        this.close.accept(this);
        if (this.overlay == overlay) this.overlay = null;
    }
    
    public void close() {
        this.close.accept(this);
    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        for (AbstractButtonWidget btn : buttons) {
            btn.render(matricies, mouseX, mouseY, delta);
        }
        if (this.overlay != null) this.overlay.render(matricies, mouseX, mouseY, delta);
    }

}
