package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AbstractFurnaceContainer.class)
public interface MixinAbstractFurnaceScreenHandler {

    @Invoker
    boolean invokeIsSmeltable(ItemStack itemStack);

    @Invoker
    boolean invokeIsFuel(ItemStack itemStack);

    @Accessor
    PropertyDelegate getPropertyDelegate();

}
