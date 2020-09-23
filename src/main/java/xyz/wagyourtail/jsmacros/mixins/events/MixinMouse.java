package xyz.wagyourtail.jsmacros.mixins.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Mouse;
import net.minecraft.util.ActionResult;

import xyz.wagyourtail.jsmacros.events.KeyCallback;

@Mixin(Mouse.class)
class MixinMouse {
    
    @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
    private void onMouseButton(long window, int key, int action, int mods, final CallbackInfo info) {
        ActionResult result = KeyCallback.EVENT.invoker().interact(window, key, -1, action, mods);
        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }
}