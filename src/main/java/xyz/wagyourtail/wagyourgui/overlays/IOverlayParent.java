package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.gui.Element;
import xyz.wagyourtail.wagyourgui.containers.IContainerParent;

import javax.annotation.Nullable;

public interface IOverlayParent extends IContainerParent {

    void closeOverlay(OverlayContainer overlay);

    void setFocused(@Nullable Element focused);

    OverlayContainer getChildOverlay();

}
