package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.text.Text;

public interface IChatHud {

    void jsmacros_addMessageBypass(Text message);

    void jsmacros_addMessageAtIndexBypass(Text message, int index, int time);

}
