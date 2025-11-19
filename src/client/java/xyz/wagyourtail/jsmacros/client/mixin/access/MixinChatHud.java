package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud {

    @Shadow
    private void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
    }

    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @Override
    public void jsmacros_addMessageBypass(Text message) {
        addMessage(message, null, MessageIndicator.system());
    }

    @Unique
    ThreadLocal<Integer> jsmacros$positionOverride = ThreadLocal.withInitial(() -> 0);

    @Override
    public void jsmacros_addMessageAtIndexBypass(Text message, int index, int time) {
        jsmacros$positionOverride.set(index);
        addMessage(message, null, MessageIndicator.system());
        jsmacros$positionOverride.set(0);
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", remap = false))
    public int overrideMessagePos(int pos) {
        return jsmacros$positionOverride.get();
    }


}
