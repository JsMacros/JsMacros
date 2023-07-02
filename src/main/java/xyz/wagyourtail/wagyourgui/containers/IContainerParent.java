package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public interface IContainerParent {

    <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    void remove(Element button);

    void openOverlay(OverlayContainer overlay);

    void openOverlay(OverlayContainer overlay, boolean disableButtons);

    IOverlayParent getFirstOverlayParent();

}
