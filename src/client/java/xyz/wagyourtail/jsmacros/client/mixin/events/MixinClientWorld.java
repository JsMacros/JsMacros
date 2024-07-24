package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventEntityLoad;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventEntityUnload;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(at = @At("TAIL"), method = "addEntity")
    public void onAddEntity(Entity entity, CallbackInfo ci) {
        new EventEntityLoad(entity).trigger();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onRemoved()V"), method = "removeEntity", locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRemoveEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci, Entity entity) {
        new EventEntityUnload(entity, removalReason).trigger();
    }

}
