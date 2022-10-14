package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public interface IContainerParent {

    <T extends  GuiButton > T addButton(T drawableElement);

    default <T extends GuiButton> T addDrawableChild(T drawableElement) {
        return addButton(drawableElement);
    }

    void removeButton(GuiButton button);
    
    void openOverlay(OverlayContainer overlay);
    
    void openOverlay(OverlayContainer overlay, boolean disableButtons);
    
    IOverlayParent getFirstOverlayParent();
}
