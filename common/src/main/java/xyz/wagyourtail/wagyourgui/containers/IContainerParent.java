package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.gui.widget.ButtonWidget;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public interface IContainerParent {

    <T extends ButtonWidget> T addButton(T drawableElement);

    default <T extends ButtonWidget> T addDrawableChild(T drawableElement) {
        return addButton(drawableElement);
    }

    void removeButton(ButtonWidget button);
    
    void openOverlay(OverlayContainer overlay);
    
    void openOverlay(OverlayContainer overlay, boolean disableButtons);
    
    IOverlayParent getFirstOverlayParent();
}
