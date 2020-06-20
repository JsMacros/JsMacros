package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.InGameHud;
import xyz.wagyourtail.jsmacros.runscript.classes.OverlayHud;

@Mixin(InGameHud.class)
class jsmacros_InGameHudMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void jsMacros_renderHud(final CallbackInfo info) {
        
        for (OverlayHud h : OverlayHud.instances) {
            h.render();
        }
        
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
    }
}
