package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRiding;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addPassenger(Lnet/minecraft/entity/Entity;)V"))
    public void onStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        new EventRiding(true, entity);
    }

}
