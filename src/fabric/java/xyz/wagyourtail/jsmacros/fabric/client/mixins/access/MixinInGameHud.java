package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

import java.util.Comparator;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
class MixinInGameHud {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"), method = "method_55807")
    public void renderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays).stream().sorted(Comparator.comparingInt(IDraw2D::getZIndex)).collect(Collectors.toList())) {
            try {
                h.render(context);
            } catch (Throwable ignored) {
            }
        }
    }

}
