package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventTitle;

@Mixin(MessageHandler.class)
public class MixinMessageHandler {

    @ModifyArg(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setOverlayMessage(Lnet/minecraft/text/Text;Z)V"))
    private Text modifyOverlayMessage(Text text) {
        EventTitle et = new EventTitle("ACTIONBAR", text);
        et.trigger();
        if (et.message == null || et.isCanceled()) {
            return null;
        } else {
            return et.message.getRaw();
        }
    }

}
