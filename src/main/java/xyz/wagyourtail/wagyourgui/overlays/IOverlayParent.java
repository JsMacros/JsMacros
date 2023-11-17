package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.gui.Element;
import xyz.wagyourtail.wagyourgui.containers.IContainerParent;

import org.jetbrains.annotations.Nullable;
public interface IOverlayParent extends IContainerParent {

    void closeOverlay(OverlayContainer overlay);

    void setFocused(Element focused);

    OverlayContainer getChildOverlay();

}
