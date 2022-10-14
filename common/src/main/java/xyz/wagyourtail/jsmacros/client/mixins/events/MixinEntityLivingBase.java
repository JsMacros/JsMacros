package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRiding;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    @Inject(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;startRiding(Lnet/minecraft/entity/Entity;)V"))
    public void onStartRiding(Entity entity, CallbackInfo cir) {
        new EventRiding(true, entity);
    }

    @Inject(method = "func_110145_l", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;refreshPositionAfterTeleport(DDD)V", ordinal = 1))
    public void onStopRiding(Entity p_110145_1_, CallbackInfo ci) {
        new EventRiding(false, p_110145_1_);
    }
}
