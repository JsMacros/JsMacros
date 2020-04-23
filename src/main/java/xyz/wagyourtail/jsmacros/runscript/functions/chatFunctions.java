package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;

public class chatFunctions {
    public void log(String message) {
        MinecraftClient mc = jsMacros.getMinecraft();
        LiteralText text = new LiteralText(message);
        mc.inGameHud.getChatHud().addMessage(text);
    }
    
    public void say(String message) {
        MinecraftClient mc = jsMacros.getMinecraft();
        mc.player.sendChatMessage(message);
    }
}
