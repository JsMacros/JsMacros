package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

@Mixin(GuiIngame.class)
class MixinInGameHud {
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;debugEnabled:Z"), method = "render")
    public void renderHud(float f, final CallbackInfo info) {
        
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays)) {
            try {
                h.render();
            } catch (Throwable ignored) {}
        }
    
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlphaTest();
    }
}
