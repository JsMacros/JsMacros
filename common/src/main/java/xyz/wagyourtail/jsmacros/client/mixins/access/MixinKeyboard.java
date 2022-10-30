package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Screen.class)
public abstract class MixinKeyboard {


    @Shadow
    public static boolean hasShiftDown() {
        return false;
    }

    @Shadow
    public static boolean hasControlDown() {
        return false;
    }

    @Shadow
    public static boolean hasAltDown() {
        return false;
    }

    @Unique
    private static int createModifiers() {
        int i = 0;
        if (hasShiftDown())
            i |= 1;
        if (hasControlDown())
            i |= 2;
        if (hasAltDown())
            i |= 4;
        return i;
    }

    @Inject(method = "handleKeyboard", at = @At("HEAD"))
    private void onKeyPressed(CallbackInfo ci) {
        if (Keyboard.getEventKeyState())
            ((IScreenInternal) this).jsmacros_keyPressed(Keyboard.getEventKey(), 0, createModifiers());
    }

//    @Redirect(method = "method_1458", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z"))
//    private static boolean onCharTyped(Element instance, char chr, int modifiers) {
//        ((IScreenInternal) instance).jsmacros_charTyped((char) chr, modifiers);
//        return instance.charTyped(chr, modifiers);
//    }
}
