package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.function.Predicate;

public interface IChatHud {
    
    void jsmacros_addMessageBypass(IChatComponent message);

    List<ChatLine> jsmacros_getMessages();

    void jsmacros_removeMessageById(int messageId);

    void jsmacros_addMessageAtIndexBypass(IChatComponent message, int index, int time);

    void jsmacros_removeMessage(int index);

    void jsmacros_removeMessageByText(IChatComponent text);

    void jsmacros_removeMessagePredicate(Predicate<ChatLine> textfilter);
}
