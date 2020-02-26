package xyz.wagyourtail.jsmacros.events.mixins;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.events.RecieveMessageCallback;

@Mixin(InGameHud.class)
class  jsmacros_InGameHudMixin {
    
    @Shadow @Final
    private Map<MessageType, List<ClientChatListener>> listeners;
    
    @SuppressWarnings("rawtypes")
    @Inject(at = @At("HEAD"), method = "addChatMessage", cancellable = true)
    private void jsmacros_addChatMessage(MessageType messageType, Text text, CallbackInfo info) {
        String result = RecieveMessageCallback.EVENT.invoker().interact(text.asFormattedString());
        if (result == null || result.equals("")) {
            info.cancel();
        } else if (!text.asFormattedString().equals(result)) {
            info.cancel();
            Iterator var3 = ((List)this.listeners.get(messageType)).iterator();
            Text msg = new LiteralText(result);
            
            while(var3.hasNext()) {
               ClientChatListener clientChatListener = (ClientChatListener)var3.next();
               clientChatListener.onChatMessage(messageType, msg);
            }
        }
    }
}