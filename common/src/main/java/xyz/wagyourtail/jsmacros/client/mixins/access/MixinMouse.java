package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;

@Mixin(Screen.class)
public class MixinMouse {
    @Unique
    private int jsmacros_prevX = 0;
    @Unique
    private int jsmacros_prevY = 0;

    @Inject(method = "handleMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(III)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onMouseClicked(CallbackInfo ci, int i, int j, int k) {
        jsmacros_prevX = i;
        jsmacros_prevY = j;
        ((IScreenInternal) this).jsmacros_mouseClicked(i, j, k);
    }
    @Inject(method = "handleMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(III)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onMouseReleased(CallbackInfo ci, int d, int e, int i) {
        ((IScreenInternal) this).jsmacros_mouseReleased(d, e, i);
    }


    @Inject(method = "handleMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(IIIJ)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onMouseDragged(CallbackInfo ci, int d, int e, int i, long l) {
        ((IScreenInternal) this).jsmacros_mouseDragged(d, e, i, jsmacros_prevX, jsmacros_prevY);
        jsmacros_prevX = d;
        jsmacros_prevY = e;
    }

    @Inject(method = "handleMouse", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onMouseScrolled(CallbackInfo ci, int i, int j) {
        int dw = Mouse.getEventDWheel();
        if (dw != 0) {
            ((IScreenInternal) this).jsmacros_mouseScrolled(i, j, (int) (dw / 60D));
            ((IScreenInternal) this).mouseScrolled(i, j, (int) (dw / 60D));
        }
    }
}
