package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventMouseScroll;

@Mixin(Mouse.class)
class MixinMouse {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
    private void onMouseButton(long window, int key, int action, int mods, final CallbackInfo info) {
        if (window != client.getWindow().getHandle()) {
            return;
        }
        if (key == -1 || action == 2) {
            return;
        }
        if (EventKey.parse(key, -1, action, mods)) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onMouseScroll", cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window != client.getWindow().getHandle()) return;
        if (client.getOverlay() != null || client.currentScreen != null || client.player == null) return;
        if (vertical == 0.0 && horizontal == 0.0) return;
        EventMouseScroll event = new EventMouseScroll(horizontal, vertical);
        event.trigger();
        if (event.isCanceled()) ci.cancel();
    }

}
