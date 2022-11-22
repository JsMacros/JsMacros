package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Mixin(GuiNewChat.class)
public abstract class MixinChatHud implements IChatHud {

    @Shadow public abstract void addMessage(IChatComponent chatComponent, int chatLineId);

    @Mutable
    @Shadow @Final private List<ChatLine> messages;

    @Shadow public abstract void removeMessage(int p_146242_1_);

    @Shadow protected abstract void addMessage(IChatComponent chatComponent, int chatLineId, int p_146237_3_, boolean p_146237_4_);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft client, CallbackInfo ci) {
        messages = Collections.synchronizedList(messages);
    }

    @Override
    public void jsmacros_addMessageBypass(IChatComponent message) {
        addMessage(message, 0);
    }

    @Override
    public List<ChatLine> jsmacros_getMessages() {
        return messages;
    }

    @Override
    public void jsmacros_removeMessageById(int messageId) {
        removeMessage(messageId);
    }

    @Unique
    ThreadLocal<Integer> positionOverride = ThreadLocal.withInitial(() -> 0);


    @Override
    public void jsmacros_addMessageAtIndexBypass(IChatComponent message, int index, int time) {
        positionOverride.set(index);
        addMessage(message, 0, time, false);
        positionOverride.set(0);
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/util/IChatComponent;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1,  remap = false))
    public int overrideMessagePos(int pos) {
        return positionOverride.get();
    }

    @Override
    public void jsmacros_removeMessage(int index) {
        messages.remove(index);
    }

    @Override
    public void jsmacros_removeMessageByText(IChatComponent text) {
        messages.removeIf((c) -> c.getText().equals(text));
    }

    @Override
    public void jsmacros_removeMessagePredicate(Predicate<ChatLine> textfilter) {
        messages.removeIf(textfilter);
    }

}


