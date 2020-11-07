package xyz.wagyourtail.jsmacros.mixins.access;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.access.IChatHud;

@Mixin(ChatHud.class)
public class MixinChatHud implements IChatHud {

    @Shadow
    private void addMessage(Text message, int messageId) {}
    
    @Override
    public void jsmacros_addMessageBypass(Text message) {
        addMessage(message, 0);
    }

}
