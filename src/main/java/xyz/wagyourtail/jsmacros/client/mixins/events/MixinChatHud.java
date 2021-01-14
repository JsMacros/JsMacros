package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRecvMessage;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

@Mixin(ChatHud.class)
class MixinChatHud {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At(value = "HEAD"))
    private Text modifyChatMessage(Text text) {
        if (text == null) return text;
        final TextHelper result = new EventRecvMessage(text).text;
        if (result == null) return null;
        if (!result.getRaw().equals(text)) {
            return result.getRaw();
        }
        else return text;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onAddChatMessage(Text text, CallbackInfo info) {
        if (text == null) {
            info.cancel();
        }
    }
}