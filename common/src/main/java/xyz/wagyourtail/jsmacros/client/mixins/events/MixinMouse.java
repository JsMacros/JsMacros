package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;

@Mixin(Mouse.class)
class MixinMouse {
    
    @Inject(at = @At("HEAD"), method = "onMouseButton")
    private void onMouseButton(long window, int key, int action, int mods, final CallbackInfo info) {
        if (key == -1 || action == 2) return;
        new EventKey(key, -1, action, mods);
    }
}