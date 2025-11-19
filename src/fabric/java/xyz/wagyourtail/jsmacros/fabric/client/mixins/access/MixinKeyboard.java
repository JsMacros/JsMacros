package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @WrapOperation(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"))
    private boolean onKeyPressed(Screen instance, int keyCode, int scanCode, int modifiers, Operation<Boolean> original) {
        ((IScreenInternal) instance).jsmacros_keyPressed(keyCode, scanCode, modifiers);
        return original.call(instance, keyCode, scanCode, modifiers);
    }

    @WrapOperation(method = "onChar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;charTyped(CI)Z"))
    private boolean onCharTyped1(Screen instance, char c, int i, Operation<Boolean> original) {
        ((IScreenInternal) instance).jsmacros_charTyped(c, i);
        return original.call(instance, c, i);
    }

}
