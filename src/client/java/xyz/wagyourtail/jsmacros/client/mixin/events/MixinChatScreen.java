package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSendMessage;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen {

    //IGNORE
    protected MixinChatScreen(Text title) {
        super(title);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        final EventSendMessage event = new EventSendMessage(chatText);
        event.trigger();
        if (event.message == null || event.message.equals("") || event.isCanceled()) {
            ci.cancel();
        } else if (!event.message.equals(chatText)) {
            ci.cancel();
            assert this.client != null;
            assert this.client.player != null;
            if (event.message.startsWith("/")) {
                this.client.player.networkHandler.sendChatCommand(event.message.substring(1));
            } else {
                this.client.player.networkHandler.sendChatMessage(event.message);
            }
        }
    }

}
