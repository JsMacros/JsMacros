package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;

public class chatFunctions {
    public void log(String message) {
        LiteralText text = new LiteralText(message);
        jsMacros.getMinecraft().inGameHud.getChatHud().addMessage(text);
    }
    
    public void say(String message) {
        jsMacros.getMinecraft().player.sendChatMessage(message);
    }
}
