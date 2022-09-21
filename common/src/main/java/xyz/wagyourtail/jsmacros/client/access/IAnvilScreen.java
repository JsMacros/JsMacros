package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.widget.TextFieldWidget;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface IAnvilScreen {

    void jsmacros_rename(String name);

    TextFieldWidget jsmacros_getRenameText();
    
}
