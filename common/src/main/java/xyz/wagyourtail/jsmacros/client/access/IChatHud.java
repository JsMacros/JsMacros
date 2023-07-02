package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Predicate;

public interface IChatHud {
    
    void jsmacros_addMessageBypass(Text message);

    List<ChatHudLine> jsmacros_getMessages();

    void jsmacros_removeMessageById(int messageId);

    void jsmacros_addMessageAtIndexBypass(Text message, int index, int time);

    void jsmacros_removeMessage(int index);

    void jsmacros_removeMessageByText(Text text);

    void jsmacros_removeMessagePredicate(Predicate<ChatHudLine> textfilter);
}
