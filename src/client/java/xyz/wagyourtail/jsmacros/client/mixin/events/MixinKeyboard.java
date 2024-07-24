package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;

@Mixin(Keyboard.class)
class MixinKeyboard {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int mods, final CallbackInfo info) {
        if (window != client.getWindow().getHandle()) {
            return;
        }
        if (key == -1 || action == 2) {
            return;
        }
        if (EventKey.parse(key, scancode, action, mods)) {
            info.cancel();
        }
    }

}
