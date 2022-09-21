package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.slot.Slot;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ICreativeInventoryScreen {

    int jsmacros_getSelectedTab();

    float jsmacros_getScrollPosition();

    TextFieldWidget jsmacros_getSearchField();

    Slot jsmacros_getDeleteItemSlot();
    
    void jsmacros_setSelectedTab(int tab);
    
    void jsmacros_search();

}
