package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.compat.interfaces.IChatHud;

@Mixin(ChatHud.class)
public class jsmacros_ChatHudMixin implements IChatHud {

    @Shadow
    private void addMessage(Text message, int messageId) {}
    
    @Override
    public void addMessageBypass(Text message) {
        addMessage(message, 0);
    }

}
