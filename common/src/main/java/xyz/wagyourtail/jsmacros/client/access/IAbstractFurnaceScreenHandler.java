package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.item.ItemStack;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface IAbstractFurnaceScreenHandler {

    boolean jsmacros_isSmeltable(ItemStack stack);

    boolean jsmacros_isFuel(ItemStack stack);
    
}
