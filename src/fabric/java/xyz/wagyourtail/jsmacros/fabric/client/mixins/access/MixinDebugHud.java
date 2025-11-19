package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

import java.util.Comparator;

@Mixin(DebugHud.class)
class MixinDebugHud {
    @Inject(
            method = "drawLeftText(Lnet/minecraft/client/gui/DrawContext;)V",
            at = @At("TAIL")
    )
    private void afterDrawLeftText(DrawContext context, CallbackInfo ci) {
        DebugHud self = (DebugHud) (Object) this;
        if (!self.shouldShowDebugHud()) return;

        ImmutableSet.copyOf(FHud.overlays).stream()
                .sorted(Comparator.comparingInt(IDraw2D::getZIndex))
                .forEachOrdered(hud -> hud.render(context));
    }
}
