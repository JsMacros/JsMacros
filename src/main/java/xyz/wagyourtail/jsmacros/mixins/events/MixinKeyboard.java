package xyz.wagyourtail.jsmacros.mixins.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import xyz.wagyourtail.jsmacros.api.events.EventKey;

@Mixin(Keyboard.class)
class MixinKeyboard {
    
    @Inject(at = @At("HEAD"), method = "onKey")
    private void onKey(long window, int key, int scancode, int action, int mods, final CallbackInfo info) {
        if (key == -1 || action == 2) return;
        new EventKey(key, scancode, action, mods);
    }
}