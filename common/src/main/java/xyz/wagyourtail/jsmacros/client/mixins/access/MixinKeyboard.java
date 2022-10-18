package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "method_1454", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"))
    private void onKeyPressed(int i, Screen screen, boolean[] bls, int j, int k, int l, CallbackInfo ci) {
        ((IScreenInternal) screen).jsmacros_keyPressed(j, k, l);
    }

    @Inject(method = "method_1458", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z"))
    private static void onCharTyped(Element element, int i, int j, CallbackInfo ci) {
        ((IScreenInternal) element).jsmacros_charTyped((char) i, j);
    }
}
