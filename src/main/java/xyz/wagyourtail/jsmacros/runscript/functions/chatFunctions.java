package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public class chatFunctions {
    private void logInternal(String message) {
        if (message != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            LiteralText text = new LiteralText(message);
            mc.inGameHud.getChatHud().addMessage(text, 0);
        }
    }
    
    private void logInternal(TextHelper text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.getChatHud().addMessage(text.getRaw(), 0);
    }
    
    // yay, auto type coercion.
    public void log(Object message) {
        if (message instanceof TextHelper) {
            this.logInternal((TextHelper)message);
        } else if (message != null) {
            this.logInternal(message.toString());
        }
    }
    
    public void say(String message) {
        if (message != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.sendChatMessage(message);
        }
    }
    
    public TextHelper createTextHelperFromString(String content) {
        return new TextHelper(new LiteralText(content));
    }
    
    public TextHelper createTextHelperFromJSON(String json) {
        return new TextHelper(json);
    }
}
