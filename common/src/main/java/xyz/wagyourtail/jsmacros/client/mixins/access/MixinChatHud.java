package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
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
    public void addMessage(Text message, int messageId) {}

    @Shadow
    private void addMessage(Text message, int messageId, int timestamp, boolean refresh) {}

    @Shadow @Final private List<ChatHudLine> messages;

    @Shadow
    public abstract void removeMessage(int messageId);

    @Mutable
    @Shadow @Final private List<String> messageHistory;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(MinecraftClient client, CallbackInfo ci) {
        messageHistory = Collections.synchronizedList(messageHistory);
    }

    @Override
    public void jsmacros_addMessageBypass(Text message) {
        addMessage(message, 0);
    }

    @Override
    public List<ChatHudLine> jsmacros_getMessages() {
        return messages;
    }

    @Override
    public void jsmacros_removeMessageById(int messageId) {
        removeMessage(messageId);
    }

    @Unique
    ThreadLocal<Integer> positionOverride = ThreadLocal.withInitial(() -> 0);


    @Override
    public void jsmacros_addMessageAtIndexBypass(Text message, int index, int time) {
        positionOverride.set(index);
        addMessage(message, 0, time, false);
        positionOverride.set(0);
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1))
    public int overrideMessagePos(int pos) {
        return positionOverride.get();
    }

    @Override
    public void jsmacros_removeMessage(int index) {
        messages.remove(index);
    }

    @Override
    public void jsmacros_removeMessageByText(Text text) {
        messages.removeIf((c) -> c.getText().equals(text));
    }

    @Override
    public void jsmacros_removeMessagePredicate(Predicate<ChatHudLine> textfilter) {
        messages.removeIf(textfilter);
    }

}
