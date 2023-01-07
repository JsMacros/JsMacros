package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSendMessage;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen {

    //IGNORE
    protected MixinChatScreen(Text title) {
        super(title);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        final String result = new EventSendMessage(chatText).message;
        if (result == null || result.equals("")) {
            cir.setReturnValue(true);
        } else if (!result.equals(chatText)) {
            cir.setReturnValue(true);
            if (result.startsWith("/")) {
                this.client.player.sendCommand(result.substring(1));
            } else {
                this.client.player.sendChatMessage(result, null);
            }
        }
    }
}
