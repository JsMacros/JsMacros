package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class chatFunctions {
    public void log(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        LiteralText text = new LiteralText(message);
        mc.inGameHud.getChatHud().addMessage(text);
    }
    
    public void say(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.player.sendChatMessage(message);
    }
}
