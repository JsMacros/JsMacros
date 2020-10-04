package xyz.wagyourtail.jsmacros.gui.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public class OverlayContainer extends MultiElementContainer {
    public Map<AbstractButtonWidget, Boolean> savedBtnStates = new HashMap<>();
    public Scrollbar scroll;
    protected Consumer<OverlayContainer> close;
    protected Consumer<AbstractButtonWidget> removeButton;
    protected OverlayContainer overlay;
    
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
    
    public OverlayContainer getChildOverlay() {
        if (overlay != null) return overlay.getChildOverlay();
        else return this;
    }
    
    public void closeOverlay(OverlayContainer overlay) {
        if (this.overlay != null && this.overlay == overlay) {
            this.close.accept(overlay);
            this.overlay = null;
        }
        else this.close.accept(overlay);
    }
    
    public void close() {
        this.close.accept(this);
    }
    
    public void renderBackground(MatrixStack matricies) {
        // black bg
        fill(matricies, x, y, x + width, y + height, 0xFF000000);
        // 2 layer border
        fill(matricies, x, y, x + width, y + 1, 0x7F7F7F7F);
        fill(matricies, x, y + height - 1, x + width, y + height, 0x7F7F7F7F);
        fill(matricies, x, y + 1, x + 1, y + height - 1, 0x7F7F7F7F);
        fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0x7F7F7F7F);

        fill(matricies, x + 1, y + 1, x + width - 1, y + 2, 0xFFFFFFFF);
        fill(matricies, x + 1, y + height - 2, x + width - 1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);

    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        for (AbstractButtonWidget btn : buttons) {
            btn.render(matricies, mouseX, mouseY, delta);
        }
        if (this.overlay != null) this.overlay.render(matricies, mouseX, mouseY, delta);
    }

}
