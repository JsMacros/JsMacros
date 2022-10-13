package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.gui.Element;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.wagyourgui.containers.IContainerParent;


public interface IOverlayParent extends IContainerParent {
    
    void closeOverlay(OverlayContainer overlay);
    
    void setFocused(@Nullable Element focused);
    
    OverlayContainer getChildOverlay();
}
