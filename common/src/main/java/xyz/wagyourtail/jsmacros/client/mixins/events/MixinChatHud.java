package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRecvMessage;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

@Mixin(ChatHud.class)
class MixinChatHud {
    @Unique
    private EventRecvMessage eventRecvMessage;
    @Unique
    private Text originalMessage;

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD")
    )
    private void onAddMessage1(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        originalMessage = message;
        eventRecvMessage = new EventRecvMessage(message, signature, indicator);
    }

    @Unique
    private boolean modifiedEventRecieve;

    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At(value = "HEAD"),
        argsOnly = true
    )
    private Text modifyChatMessage(Text text) {
        modifiedEventRecieve = false;
        if (text == null) return null;
        final TextHelper result = eventRecvMessage.text;
        if (result == null) return null;
        if (!result.getRaw().equals(text)) {
            modifiedEventRecieve = true;
            return result.getRaw();
        } else {
            return text;
        }
    }

    @Unique
    private final Text MODIFIED_TEXT = Text.translatable("jsmacros.chat.tag.modified").formatted(Formatting.UNDERLINE);

    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At(value = "HEAD"),
        argsOnly = true
    )
    private MessageIndicator modifyChatMessageSignature(MessageIndicator signature) {
        if (modifiedEventRecieve) {
            MutableText text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK);
            if (signature != null && signature.text() != null) {
                text2.append(originalMessage).append(ScreenTexts.LINE_BREAK).append(signature.text());
            } else {
                text2.append(originalMessage);
            }
            return new MessageIndicator(15386724, MessageIndicator.Icon.CHAT_MODIFIED, text2, "Modified");
        } else {
            return signature;
        }
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