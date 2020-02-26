package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Mouse;
import net.minecraft.util.ActionResult;

import xyz.wagyourtail.jsmacros.events.KeyCallback;

@Mixin(Mouse.class)
class jsmacros_MouseMixin {
    
    @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
    private void jsmacros_onMouseButtonMixin(long window, int key, int action, int mods, final CallbackInfo info) {
        ActionResult result = KeyCallback.EVENT.invoker().interact(window, key, -1, action, mods);
        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }
}