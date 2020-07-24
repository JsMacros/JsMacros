package xyz.wagyourtail.jsmacros.compat.mixins;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(MinecraftClient.class)
class jsmacros_MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method="onResolutionChanged")
    public void jsmacros_onResolutionChanged(CallbackInfo info) {
        
        ArrayList<Draw2D> overlays;
        
        try {
            overlays = new ArrayList<>(hudFunctions.overlays);
        } catch(Exception e) {
            return;
        }
        
        for (Draw2D h : overlays) {
            h.init();
        }
    }
}
