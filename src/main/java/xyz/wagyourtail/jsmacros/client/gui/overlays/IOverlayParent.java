package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import xyz.wagyourtail.jsmacros.client.gui.containers.IContainerParent;

import javax.annotation.Nullable;

public interface IOverlayParent extends IContainerParent {

    void removeButton(AbstractButtonWidget button);
    
    void closeOverlay(OverlayContainer overlay);
    
    void setFocused(@Nullable Element focused);

}
