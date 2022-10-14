package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityLoad;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityUnload;

@Mixin(WorldClient.class)
public class MixinClientWorld {

    @Inject(at = @At("TAIL"), method = "addEntity")
    public void onAddEntity(int id, Entity entity, CallbackInfo ci) {
        new EventEntityLoad(entity);
    }

    @Inject(at = @At("HEAD"), method = "removeEntity(Lnet/minecraft/entity/Entity;)V")
    public void onRemoveEntity(Entity entity, CallbackInfo ci) {
        new EventEntityUnload(entity);
    }

    @Inject(at = @At("HEAD"), method = "removeEntity(I)Lnet/minecraft/entity/Entity;")
    public void onRemoveEntity(int id, CallbackInfoReturnable<Entity> cir) {
        new EventEntityUnload(cir.getReturnValue());
    }
}
