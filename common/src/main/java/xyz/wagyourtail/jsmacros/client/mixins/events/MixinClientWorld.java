package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityLoad;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityUnload;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(at = @At("TAIL"), method = "addEntityPrivate")
    public void onAddEntity(int id, Entity entity, CallbackInfo ci) {
        new EventEntityLoad(entity);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;remove()V"), method = "removeEntity", locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRemoveEntity(int entityId, CallbackInfo ci, Entity entity) {
        new EventEntityUnload(entity);
    }
}
