package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.events.ItemDamageCallback;

@Mixin(ItemStack.class)
abstract class jsmacros_ItemStackMixin {
    
    @Shadow
    public abstract int getDamage();
    
    @Inject(at = @At("HEAD"), method="setDamage")
    private void jsmacros_setDamage(int damage, CallbackInfo info) {
        if (jsMacros.getMinecraft().player != null && jsMacros.getMinecraft().player.inventory != null)
            if (damage != 0 && jsMacros.getMinecraft().player.inventory.contains((ItemStack) (Object) this) ) {
                ItemDamageCallback.EVENT.invoker().interact((ItemStack) (Object) this, damage);
            }
    }
}
