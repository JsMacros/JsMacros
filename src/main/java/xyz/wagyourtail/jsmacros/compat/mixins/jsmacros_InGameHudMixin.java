package xyz.wagyourtail.jsmacros.compat.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(InGameHud.class)
class jsmacros_InGameHudMixin {
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z"), method = "render")
    public void jsMacros_renderHud(MatrixStack matrixStack, float f, final CallbackInfo info) {

        List<Draw2D> overlays;

        try {
            overlays = new ArrayList<>(hudFunctions.overlays);
        } catch (Exception e) {
            return;
        }

        for (Draw2D h : overlays) {
            try {
                h.render(matrixStack);
            } catch (Exception e) {}
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
    }
}
