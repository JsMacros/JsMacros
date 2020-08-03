package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.GameRenderer;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(GameRenderer.class)
public class jsmacros_GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void jsmacros_render(float tickDelta, long startTime, boolean tick, CallbackInfo info) {

        Runnable runnable;
        while ((runnable = (Runnable) hudFunctions.renderTaskQueue.poll()) != null) {
            runnable.run();
        }
    }
}
