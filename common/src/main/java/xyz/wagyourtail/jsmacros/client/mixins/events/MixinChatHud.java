package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRecvMessage;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

import java.util.concurrent.Semaphore;

@Mixin(ChatHud.class)
class MixinChatHud {
    @Unique
    private EventRecvMessage eventRecvMessage;

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD")
    )
    private void onAddMessage1(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        eventRecvMessage = new EventRecvMessage(message, signature, indicator);
    }

    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At(value = "HEAD"),
        argsOnly = true
    )
    private Text modifyChatMessage(Text text) {
        if (text == null) return null;
        final TextHelper result = eventRecvMessage.text;
        if (result == null) return null;
        if (!result.getRaw().equals(text)) {
            return result.getRaw();
        }
        else return text;
    }

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAddChatMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        if (message == null) {
            ci.cancel();
        }
    }
}