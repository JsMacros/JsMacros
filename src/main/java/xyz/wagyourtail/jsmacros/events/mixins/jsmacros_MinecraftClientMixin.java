package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import xyz.wagyourtail.jsmacros.events.DimensionChangeCallback;

@Mixin(MinecraftClient.class)
public class jsmacros_MinecraftClientMixin {

    @Inject(at = @At("HEAD"), method="joinWorld")
    public void jsmacros_joinWorld(ClientWorld world, CallbackInfo info) {
        if (world != null)
            DimensionChangeCallback.EVENT.invoker().interact(world.getDimensionRegistryKey().getValue().toString());
    }
}
