package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public interface IContainerParent {

    <T extends  AbstractButtonWidget > T addButton(T drawableElement);

    default <T extends AbstractButtonWidget> T addDrawableChild(T drawableElement) {
        return addButton(drawableElement);
    }

    void removeButton(AbstractButtonWidget button);
    
    void openOverlay(OverlayContainer overlay);
    
    void openOverlay(OverlayContainer overlay, boolean disableButtons);
    
    IOverlayParent getFirstOverlayParent();
}
