package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;

@Mixin(value = GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At("HEAD"), method = "updateCrosshairTarget", cancellable = true)
    public void onTargetUpdate(float tickDelta, CallbackInfo ci) {
        if (InteractionProxy.Target.onUpdate(tickDelta)) {
            ci.cancel();
        }
    }

}
