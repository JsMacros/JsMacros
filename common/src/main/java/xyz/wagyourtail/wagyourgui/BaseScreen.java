package xyz.wagyourtail.wagyourgui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.elements.TextInput;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen extends GuiScreen implements IOverlayParent {
    public GuiScreen parent;
    protected OverlayContainer overlay;
    protected IChatComponent title;
    private final int[] prevMouseX = new int[] {0,0,0,0,0,0};
    private final int[] prevMouseY = new int[] {0,0,0,0,0,0};

    protected BaseScreen(IChatComponent title, GuiScreen parent) {
        super();
        this.title = title;
        this.parent = parent;
    }

    public static String trimmed(FontRenderer textRenderer, String str, int width) {
        return textRenderer.trimToWidth(str, width);
    }

    public void reload() {
        init();
    }

    @Override
    public void init() {
        assert client != null;
        buttons.clear();
        super.init();
        overlay = null;
        JsMacros.prevScreen = this;
    }

    @Override
    public void removed() {
        assert minecraft != null;
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
        if (overlay != null) return overlay.getChildOverlay();
        return null;
    }

    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        if (this.overlay != null) {
            this.overlay.openOverlay(overlay, disableButtons);
            return;
        }
        if (disableButtons) {
            for (GuiButton b : buttons) {
                overlay.savedBtnStates.put(b, b.active);
                b.active = false;
            }
        }
        this.overlay = overlay;
        overlay.init();
    }

    @Override
    public void closeOverlay(OverlayContainer overlay) {
        if (overlay == null) return;
        for (GuiButton b : overlay.getButtons()) {
            removeButton(b);
        }
        for (GuiButton b : overlay.savedBtnStates.keySet()) {
            b.active = overlay.savedBtnStates.get(b);
        }
        overlay.onClose();
        if (this.overlay == overlay) this.overlay = null;
    }

    @Override
    public void removeButton(GuiButton btn) {
        buttons.remove(btn);
    }

    @Override
    public <T extends GuiButton> T addButton(T button) {
        buttons.add(button);
        return button;
    }

    @Override
    public void handleKeyboard() throws IOException {
        if (Keyboard.getEventKeyState()) {
            if (!keyPressed(Keyboard.getEventKey(), 0, createModifiers())) {
                this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            }
        } else {
            keyReleased(Keyboard.getEventKey(), 0, createModifiers());
        }
    }

    public static int createModifiers() {
        int i = 0;
        if (GuiScreen.hasShiftDown()) i |= 1;
        if (GuiScreen.hasControlDown()) i |= 2;
        if (GuiScreen.hasAltDown()) i |= 4;
        return i;
    }

    public static List<Integer> unpackModifiers(int mods) {
        List<Integer> l = new ArrayList<>();
        if ((mods & 4) == 4) {
            l.add(Keyboard.KEY_LMETA);
        }
        if ((mods & 2) == 2) {
            l.add(Keyboard.KEY_LCONTROL);
        }
        if ((mods & 1) == 1) {
            l.add(Keyboard.KEY_LSHIFT);
        }
        return l;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (overlay != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                this.overlay.closeOverlay(this.overlay.getChildOverlay());
                return true;
            }
            if (this.overlay.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        for (GuiButton b : buttons) {
            if (b instanceof TextInput && ((TextInput) b).keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        if (overlay!= null && overlay.scroll != null) overlay.scroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return false;
    }

    public boolean mouseDragged(int mouseX, int mouseY, int button, int deltaX, int deltaY) {
        GuiButton focused = ((IScreenInternal) this).getFocused();
        if (focused instanceof Scrollbar) {
            ((Scrollbar) focused).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return true;
        }
        for (GuiButton b : buttons) {
            if (b instanceof TextInput) {
                if (((TextInput) b).selected) return ((TextInput) b).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (overlay != null) overlay.onClick(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mosueX, int mouseY, int button) {
        super.mouseReleased(mosueX, mouseY, button);
    }

    @Override
    protected void mouseDragged(int mouseX, int mouseY, int btn, long time) {
        mouseDragged(mouseX, mouseY, btn, mouseX - prevMouseX[btn], mouseY - prevMouseY[btn]);
        prevMouseX[btn] = mouseX;
        prevMouseY[btn] = mouseY;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (overlay != null) overlay.render(mouseX, mouseY, delta);
    }

    public boolean shouldCloseOnEsc() {
        return this.overlay == null;
    }

    public void updateSettings() {}

    public void onClose() {
        close();
    }

    public void close() {
        assert client != null;
        if (client.world == null)
            openParent();
        else {
            client.openScreen(null);
        }
    }

    public void openParent() {
        assert client != null;
        client.openScreen(parent);
    }

}