package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class chatFunctions {
    public void log(String message) {
        if (message != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            LiteralText text = new LiteralText(message);
            mc.inGameHud.getChatHud().addMessage(text, 0);
        }
    }
    
    public void say(String message) {
        if (message != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.sendChatMessage(message);
        }
    }
}
