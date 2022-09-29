package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface IAbstractFurnaceScreenHandler {

    PropertyDelegate jsmacros_getPropertyDelegate();
    
    boolean jsmacros_isSmeltable(ItemStack stack);

    boolean jsmacros_isFuel(ItemStack stack);
    
}
