package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;

import java.util.HashMap;
import java.util.Map;

public abstract class OverlayContainer extends MultiElementContainer<IOverlayParent> implements IOverlayParent {
    public Map<ClickableWidget, Boolean> savedBtnStates = new HashMap<>();
    public Scrollbar scroll;
    protected OverlayContainer overlay;

    public OverlayContainer(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }

    @Override
    public void remove(Element btn) {
        this.buttons.remove(btn);
        parent.remove(btn);
    }

    @Override
    public void openOverlay(OverlayContainer overlay) {
        openOverlay(overlay, true);
    }

    @Override
    public IOverlayParent getFirstOverlayParent() {
        return this;
    }

    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        if (this.overlay != null) {
            this.overlay.openOverlay(overlay, disableButtons);
            return;
        }
        if (disableButtons) {
            for (ClickableWidget b : buttons) {
                overlay.savedBtnStates.put(b, b.active);
                b.active = false;
            }
        }
        this.overlay = overlay;
        overlay.init();
    }

    @Override
    public OverlayContainer getChildOverlay() {
        if (overlay != null) {
            return overlay.getChildOverlay();
        } else {
            return this;
        }
    }

    @Override
    public void closeOverlay(OverlayContainer overlay) {
        if (this.overlay != null && this.overlay == overlay) {
            for (ClickableWidget b : overlay.getButtons()) {
                remove(b);
            }
            for (ClickableWidget b : overlay.savedBtnStates.keySet()) {
                b.active = overlay.savedBtnStates.get(b);
            }
            overlay.onClose();
            this.overlay = null;
        } else {
            parent.closeOverlay(overlay);
        }
    }

    @Override
    public void setFocused(Element focused) {
        parent.setFocused(focused);
    }

    public void onClick(double mouseX, double mouseY, int button) {
        if (overlay != null) {
            overlay.onClick(mouseX, mouseY, button);
        }
    }

    /**
     * @return true if should be handled by overlay
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (overlay != null) {
            overlay.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public void close() {
        parent.closeOverlay(this);
    }

    public void onClose() {
    }

    public void renderBackground(DrawContext drawContext) {
        // black bg
        drawContext.fill(x, y, x + width, y + height, 0xFF000000);
        // 2 layer border
        drawContext.fill(x, y, x + width, y + 1, 0x7F7F7F7F);
        drawContext.fill(x, y + height - 1, x + width, y + height, 0x7F7F7F7F);
        drawContext.fill(x, y + 1, x + 1, y + height - 1, 0x7F7F7F7F);
        drawContext.fill(x + width - 1, y + 1, x + width, y + height - 1, 0x7F7F7F7F);

        drawContext.fill(x + 1, y + 1, x + width - 1, y + 2, 0xFFFFFFFF);
        drawContext.fill(x + 1, y + height - 2, x + width - 1, y + height - 1, 0xFFFFFFFF);
        drawContext.fill(x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        drawContext.fill(x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);

    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        for (ClickableWidget btn : buttons) {
            btn.render(drawContext, mouseX, mouseY, delta);
        }
        if (this.overlay != null) {
            this.overlay.render(drawContext, mouseX, mouseY, delta);
        }
    }

}
