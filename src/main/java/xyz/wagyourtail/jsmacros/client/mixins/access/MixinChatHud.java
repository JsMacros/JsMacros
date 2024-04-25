package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.collection.ArrayListDeque;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud {

    @Shadow
    private void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
    }

    @Shadow
    @Final
    private List<ChatHudLine> messages;

//    @Shadow protected abstract void removeMessage(int messageId);

    @Shadow
    @Final
    private MinecraftClient client;

    @Override
    public void jsmacros_addMessageBypass(Text message) {
        addMessage(message, null, MessageIndicator.system());
    }

    @Override
    public List<ChatHudLine> jsmacros_getMessages() {
        return messages;
    }

    @Override
    public void jsmacros_removeMessageById(int messageId) {
        throw new UnsupportedOperationException("1.19.1 removed this method");
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

    @Override
    public void jsmacros_removeMessage(int index) {
        messages.remove(index);
    }

    @Override
    public void jsmacros_removeMessageByText(Text text) {
        messages.removeIf((c) -> c.content().equals(text));
    }

    @Override
    public void jsmacros_removeMessagePredicate(Predicate<ChatHudLine> textfilter) {
        messages.removeIf(textfilter);
    }

}
