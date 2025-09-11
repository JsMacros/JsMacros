package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;
import xyz.wagyourtail.jsmacros.client.api.classes.render.ScriptScreen;

@Mixin(value = GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void onRender(Screen instance, DrawContext drawContext, int mouseX, int mouseY, float delta) {
        instance.renderWithTooltip(drawContext, mouseX, mouseY, delta);
        if (!(client.currentScreen instanceof ScriptScreen)) {
            ((IScreenInternal) instance).jsmacros_render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Inject(at = @At("HEAD"), method = "updateCrosshairTarget", cancellable = true)
    public void onTargetUpdate(float tickDelta, CallbackInfo ci) {
        if (InteractionProxy.Target.onUpdate(tickDelta)) {
            ci.cancel();
        }
    }
}
