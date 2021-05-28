package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import xyz.wagyourtail.jsmacros.client.gui.overlays.IOverlayParent;
import xyz.wagyourtail.jsmacros.client.gui.overlays.OverlayContainer;

public interface IContainerParent {

    <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);
    
    void remove(Element button);
    
    void openOverlay(OverlayContainer overlay);
    
    void openOverlay(OverlayContainer overlay, boolean disableButtons);
    
    IOverlayParent getFirstOverlayParent();
}
