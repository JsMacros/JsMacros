package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.wagyourtail.jsmacros.events.ItemDamageCallback;

@Mixin(ItemDurabilityChangedCriterion.class)
abstract class jsmacros_ItemDurabilityChangedCriterionMixin {
    
    @Inject(at = @At("HEAD"), method="trigger")
    private void jsmacros_trigger(ServerPlayerEntity player, ItemStack stack, int damage, CallbackInfo info) {
        ItemDamageCallback.EVENT.invoker().interact(stack, damage);
    }
}
