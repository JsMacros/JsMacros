package xyz.wagyourtail.jsmacros.forge.client.mixins;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

@Mixin(GuiIngameForge.class)
class MixinInGameHud {

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z"), method = "renderHUDText")
    protected void renderHud(int wi, int he, CallbackInfo ci) {
        
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays)) {
            try {
                h.render();
            } catch (Throwable ignored) {}
        }
    
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlphaTest();
    }
}
