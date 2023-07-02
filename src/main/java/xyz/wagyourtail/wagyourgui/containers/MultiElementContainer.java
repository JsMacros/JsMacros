package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiElementContainer<T extends IContainerParent> implements IContainerParent {
    protected List<ClickableWidget> buttons = new ArrayList<>();
    protected TextRenderer textRenderer;
    protected boolean visible = true;
    public final T parent;
    public int x;
    public int y;
    public int width;
    public int height;

    public MultiElementContainer(int x, int y, int width, int height, TextRenderer textRenderer, T parent) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    public void init() {
        buttons.clear();
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        for (ClickableWidget btn : buttons) {
            btn.visible = visible;
            btn.active = visible;
        }
        this.visible = visible;
    }

    @Override
    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        buttons.add((ClickableWidget) drawableElement);
        parent.addDrawableChild(drawableElement);
        return drawableElement;
    }

    public List<ClickableWidget> getButtons() {
        return buttons;
    }

    public void setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void openOverlay(OverlayContainer overlay) {
        parent.openOverlay(overlay);
    }

    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        parent.openOverlay(overlay, disableButtons);
    }

    @Override
    public void remove(Element button) {
        this.buttons.remove(button);
        parent.remove(button);
    }

    @Override
    public IOverlayParent getFirstOverlayParent() {
        return parent.getFirstOverlayParent();
    }

    public abstract void render(DrawContext drawContext, int mouseX, int mouseY, float delta);

}
