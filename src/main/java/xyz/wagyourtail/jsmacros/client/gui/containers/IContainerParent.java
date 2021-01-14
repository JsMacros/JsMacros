package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.widget.AbstractButtonWidget;

public interface IContainerParent {
    
    <T extends AbstractButtonWidget> T  addButton(T button);

}
