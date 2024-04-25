package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow
    private int activeButton;

    @Inject(method = "method_1611", at = @At(value = "HEAD"), remap = false)
    private static void onMouseClicked(boolean[] bls, Screen screen, double x, double y, int button, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_mouseClicked(x, y, button);
    }

    @Inject(method = "method_1605", at = @At(value = "HEAD"))
    private static void onMouseReleased(boolean[] bls, Screen screen, double x, double y, int button, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_mouseReleased(x, y, button);
    }

    @Inject(method = "method_55795", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(DDIDD)Z"))
    private void onMouseDragged(Screen screen, double x, double y, double dx, double dy, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_mouseDragged(x, y, activeButton, dx, dy);
    }

    @Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z"))
    private boolean onMouseScrolled(Screen instance, double x, double y, double horiz, double vert) {
        ((IScreenInternal) instance).jsmacros_mouseScrolled(x, y, horiz, vert);
        return instance.mouseScrolled(x, y, horiz, vert);
    }

}
