package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRiding;

@Mixin(LivingEntity.class)
public class MixinEntityLivingBase {

    @Inject(method = "method_6152", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;refreshPositionAfterTeleport(DDD)V", ordinal = 1))
    public void onStopRiding(Entity p_110145_1_, CallbackInfo ci) {
        new EventRiding(false, p_110145_1_);
    }
}
