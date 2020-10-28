package xyz.wagyourtail.jsmacros.mixins.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.api.events.EventSendMessage;

@Mixin(Screen.class)
abstract class MixinScreen {

    @Shadow
    public abstract void sendMessage(String message, boolean toHud);

    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    private void onSendMessage(String message, final CallbackInfo info) {
        final String result = new EventSendMessage(message).message;
        if (result == null || result.equals("")) {
            info.cancel();
        } else if (!result.equals(message)) {
            info.cancel();
            sendMessage(result, true);
        }
    }
}
