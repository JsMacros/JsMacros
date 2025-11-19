package xyz.wagyourtail.wagyourgui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public abstract class BaseScreen extends Screen implements IOverlayParent {
    protected Screen parent;
    protected OverlayContainer overlay;

    protected BaseScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public static OrderedText trimmed(TextRenderer textRenderer, StringVisitable str, int width) {
        return Language.getInstance().reorder(textRenderer.trimToWidth(str, width));
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    public void reload() {
        init();
    }

    @Override
    protected void init() {
        assert client != null;
        clearChildren();
        super.init();
        overlay = null;
        JsMacrosClient.prevScreen = this;
    }

    @Override
    public void removed() {
        assert client != null;
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
    public OverlayContainer getChildOverlay() {
        if (overlay != null) {
            return overlay.getChildOverlay();
        }
        return null;
    }

    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        if (this.overlay != null) {
            this.overlay.openOverlay(overlay, disableButtons);
            return;
        }
        if (disableButtons) {
            for (Element b : children()) {
                if (!(b instanceof ClickableWidget)) {
                    continue;
                }
                overlay.savedBtnStates.put((ClickableWidget) b, ((ClickableWidget) b).active);
                ((ClickableWidget) b).active = false;
            }
        }
        this.overlay = overlay;
        overlay.init();
    }

    @Override
    public void closeOverlay(OverlayContainer overlay) {
        if (overlay == null) {
            return;
        }
        for (ClickableWidget b : overlay.getButtons()) {
            this.remove(b);
        }
        for (ClickableWidget b : overlay.savedBtnStates.keySet()) {
            b.active = overlay.savedBtnStates.get(b);
        }
        overlay.onClose();
        if (this.overlay == overlay) {
            this.overlay = null;
        }
    }

    @Override
    public void remove(Element btn) {
        super.remove(btn);
    }

    @Override
    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (overlay != null) {
                this.overlay.closeOverlay(this.overlay.getChildOverlay());
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horiz, double vert) {
        if (overlay != null && overlay.scroll != null) {
            overlay.scroll.mouseDragged(mouseX, mouseY, 0, 0, -vert * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, horiz, vert);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (overlay != null) {
            overlay.onClick(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (overlay != null) {
            overlay.render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.overlay == null;
    }

    public void updateSettings() {
    }

    @Override
    public void close() {
        assert client != null;
        if (client.world == null) {
            openParent();
        } else {
            setFocused(null);
            client.setScreen(null);
        }
    }

    public void openParent() {
        assert client != null;
        client.setScreen(parent);
    }

}
