package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.events.RecieveMessageCallback;

@Mixin(ChatHud.class)
class jsmacros_ChatHudMixin {

    @ModifyVariable(method = "addMessage", at = @At(value = "HEAD"))
    private Text jsmacros_addChatMessage(Text text) {
        if (text == null) return text;
        String json = Text.Serializer.toJson(text);
        String result = RecieveMessageCallback.EVENT.invoker().interact(json);
        if (result == null) return null;
        if (!json.equals(result)) {
            try {
                return Text.Serializer.fromJson(result);
            } catch(Exception e) {
                e.printStackTrace();
                return text;
            }
        }
        else return text;
    }

    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
    private void jsmacros_addChatMessage(Text text, CallbackInfo info) {
        if (text == null) {
            info.cancel();
        }
    }
}