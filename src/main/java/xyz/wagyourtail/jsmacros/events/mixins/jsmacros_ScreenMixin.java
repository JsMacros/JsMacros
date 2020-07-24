package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.events.SendMessageCallback;

@Mixin(Screen.class)
class jsmacros_ScreenMixin {
    
    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    private void jsmacros_sendMessage(String message, final CallbackInfo info) {
        String result = SendMessageCallback.EVENT.invoker().interact(message);
        if (result == null || result.equals("")) {
            info.cancel();
        } else if (!result.equals(message)) {
            info.cancel();
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.currentScreen.sendMessage(result, true);
        }
    }
}
