package xyz.wagyourtail.jsmacros.mixins.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.util.ActionResult;

import xyz.wagyourtail.jsmacros.events.KeyCallback;

@Mixin(Keyboard.class)
class MixinKeyboard {
    
    @Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
    private void onKey(long window, int key, int scancode, int i, int j, final CallbackInfo info) {
        ActionResult result = KeyCallback.EVENT.invoker().interact(window, key, scancode, i, j);
        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }
}