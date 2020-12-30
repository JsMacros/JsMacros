package xyz.wagyourtail.jsmacros.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.elements.overlays.OverlayContainer;

public class BaseScreen extends Screen {
    protected Screen parent;
    protected OverlayContainer overlay;

    protected BaseScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public void reload() {
        init();
    }

    protected void init() {
        super.init();
        buttons.clear();
        children.clear();
        overlay = null;
        JsMacros.prevScreen = this;
        client.keyboard.setRepeatEvents(true);
    }

    public void removed() {
        client.keyboard.setRepeatEvents(false);
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
        if (overlay == null) return;
        for (AbstractButtonWidget b : overlay.getButtons()) {
            removeButton(b);
        }
        for (AbstractButtonWidget b : overlay.savedBtnStates.keySet()) {
            b.active = overlay.savedBtnStates.get(b);
        }
        if (this.overlay == overlay) this.overlay = null;
    }

    public void removeButton(AbstractButtonWidget btn) {
        buttons.remove(btn);
        children.remove(btn);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (overlay != null) {
                this.overlay.closeOverlay(this.overlay.getChildOverlay());
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay!= null && overlay.scroll != null) overlay.scroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (overlay != null) overlay.render(matrices, mouseX, mouseY, delta);
    }

    public boolean shouldCloseOnEsc() {
        return this.overlay == null;
    }

    public void onClose() {
        if (client.world == null)
            openParent();
        else {
            setFocused(null);
            client.openScreen(null);
        }
    }
    
    public void openParent() {
        client.openScreen(parent);
    }
}
