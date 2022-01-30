package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

@Mixin(InGameHud.class)
class MixinInGameHud {
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"), method = "render")
    public void renderHud(MatrixStack matrixStack, float f, final CallbackInfo info) {

        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays)) {
            try {
                h.render(matrixStack);
            } catch (Throwable ignored) {}
        }
    }
}
