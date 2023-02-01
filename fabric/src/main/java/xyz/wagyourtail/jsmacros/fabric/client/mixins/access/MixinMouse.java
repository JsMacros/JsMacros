package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow private int activeButton;

    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "method_1611", at = @At(value = "HEAD"), remap = false)
    private void onMouseClicked(boolean[] bls, double d, double e, int i, CallbackInfo ci) {
        assert client.currentScreen != null;
        ((IScreenInternal) client.currentScreen).jsmacros_mouseClicked(d, e, i);
    }

    @Inject(method = "method_1605", at = @At(value = "HEAD"))
    private void onMouseReleased(boolean[] bls, double d, double e, int i, CallbackInfo ci) {
        assert client.currentScreen != null;
        ((IScreenInternal) client.currentScreen).jsmacros_mouseReleased(d, e, i);
    }

    @Inject(method = "method_1602", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;mouseDragged(DDIDD)Z"))
    private void onMouseDragged(Element element, double d, double e, double f, double g, CallbackInfo ci) {
        ((IScreenInternal) element).jsmacros_mouseDragged(d, e, activeButton, f, g);
    }

    @Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"))
    private boolean onMouseScrolled(Screen instance, double x, double y, double amount) {
        ((IScreenInternal) instance).jsmacros_mouseScrolled(x, y, amount);
        return instance.mouseScrolled(x, y, amount);
    }
}