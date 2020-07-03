package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.HungerManager;
import xyz.wagyourtail.jsmacros.events.HungerChangeCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HungerManager.class)
public class jsmacros_HungerManagerMixin {
    
    @Shadow
    private int foodLevel;
    
    @Inject(at = @At("HEAD"), method= "setFoodLevel")
    public void jsmacros_setFoodLevel(int foodLevel, CallbackInfo info) {
        if (foodLevel != this.foodLevel) {
            HungerChangeCallback.EVENT.invoker().interact(foodLevel);
        }
    }
}
