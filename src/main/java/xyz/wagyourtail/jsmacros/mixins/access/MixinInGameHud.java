package xyz.wagyourtail.jsmacros.mixins.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.api.functions.FHud;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IDraw2D;

@Mixin(InGameHud.class)
class MixinInGameHud {
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z"), method = "render")
    public void renderHud(MatrixStack matrixStack, float f, final CallbackInfo info) {

        for (IDraw2D<Draw2D> h : ImmutableList.copyOf(FHud.overlays)) {
            try {
                h.render(matrixStack);
            } catch (Exception e) {}
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
    }
}
