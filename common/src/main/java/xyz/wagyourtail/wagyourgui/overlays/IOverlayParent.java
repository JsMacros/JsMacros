package xyz.wagyourtail.wagyourgui.overlays;

import xyz.wagyourtail.wagyourgui.containers.IContainerParent;

public interface IOverlayParent extends IContainerParent {
    
    void closeOverlay(OverlayContainer overlay);
    
    OverlayContainer getChildOverlay();
}
