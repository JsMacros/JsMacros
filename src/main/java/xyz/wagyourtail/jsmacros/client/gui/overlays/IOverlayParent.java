package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.gui.Element;
import xyz.wagyourtail.jsmacros.client.gui.containers.IContainerParent;

import javax.annotation.Nullable;

public interface IOverlayParent extends IContainerParent {
    
    void closeOverlay(OverlayContainer overlay);
    
    void setFocused(@Nullable Element focused);
    
    OverlayContainer getChildOverlay();
}
