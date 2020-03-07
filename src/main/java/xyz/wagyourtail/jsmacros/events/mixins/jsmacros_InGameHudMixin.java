package xyz.wagyourtail.jsmacros.events.mixins;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.events.RecieveMessageCallback;

@Mixin(InGameHud.class)
class jsmacros_InGameHudMixin {

    @Shadow
    @Final
    private Map<MessageType, List<ClientChatListener>> listeners;

    @ModifyVariable(method = "addChatMessage", at = @At(value = "HEAD"))
    private Text jsmacros_addChatMessagee(Text text) {
        if (text == null) return text;
        String result = RecieveMessageCallback.EVENT.invoker().interact(text.asFormattedString());
        if (result == null) return new LiteralText("");
        if (!text.asFormattedString().equals(result)) return new LiteralText(result);
        else return text;
    }

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    private void jsmacros_addChatMessage(MessageType messageType, Text text, CallbackInfo info) {
        if (text == null || text.asFormattedString().equals("")) {
            info.cancel();
        } 
    }
}