package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AbstractFurnaceScreenHandler.class)
public interface MixinAbstractFurnaceScreenHandler {

    @Invoker
    boolean invokeIsSmeltable(ItemStack itemStack);

    @Invoker
    boolean invokeIsFuel(ItemStack itemStack);

    @Accessor
    PropertyDelegate getPropertyDelegate();

}
