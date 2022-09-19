package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IAbstractFurnaceScreenHandler;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class MixinAbstractFurnaceScreenHandler implements IAbstractFurnaceScreenHandler {

    @Shadow
    protected abstract boolean isSmeltable(ItemStack itemStack);

    @Shadow
    protected abstract boolean isFuel(ItemStack itemStack);

    @Override
    public boolean jsmacros_isSmeltable(ItemStack stack) {
        return isSmeltable(stack);
    }

    @Override
    public boolean jsmacros_isFuel(ItemStack stack) {
        return isFuel(stack);
    }
}
