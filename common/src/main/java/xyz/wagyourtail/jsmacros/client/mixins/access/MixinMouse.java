package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow private int activeButton;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "method_1611", at = @At(value = "HEAD"))
    private static void onMouseClicked(boolean[] bls, Screen screen, double d, double e, int i, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_mouseClicked(d, e, i);
    }

    @Inject(method = "method_1605", at = @At(value = "HEAD"))
    private static void onMouseReleased(boolean[] bls, Screen screen, double d, double e, int i, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_mouseReleased(d, e, i);
    }

    @Inject(method = "method_1602", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(DDIDD)Z"))
    private void onMouseDragged(Screen screen, double d, double e, double f, double g, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_mouseDragged(d, e, activeButton, f, g);
    }

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onMouseScrolled(long window, double horizontal, double vertical, CallbackInfo ci, double d, double e, double f) {
        assert client.currentScreen != null;
        ((IScreenInternal) client.currentScreen).jsmacros_mouseScrolled(d, e, f);
    }


}
