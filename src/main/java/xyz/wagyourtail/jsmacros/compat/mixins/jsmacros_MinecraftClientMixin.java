package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.runscript.classes.OverlayHud;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(MinecraftClient.class)
class jsmacros_MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method="onResolutionChanged")
    public void jsmacros_onResolutionChanged(CallbackInfo info) {
        for (OverlayHud h : hudFunctions.overlays) {
            h.init();
        }
    }
}
