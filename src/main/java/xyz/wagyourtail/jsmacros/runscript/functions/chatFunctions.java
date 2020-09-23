package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.access.IChatHud;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public class chatFunctions extends Functions {
    
    public chatFunctions(String libName) {
        super(libName);
    }
    
    public chatFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }

    private void logInternal(String message) {
        if (message != null) {
            LiteralText text = new LiteralText(message);
            ((IChatHud)mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
        }
    }
    
    private void logInternal(TextHelper text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ((IChatHud)mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text.getRaw());
    }
    
    // yay, auto type coercion.
    public void log(Object message) {
        mc.execute(() -> {
            if (message instanceof TextHelper) {
                this.logInternal((TextHelper)message);
            } else if (message != null) {
                this.logInternal(message.toString());
            }
        });
    }
    
    public void say(String message) {
        if (message != null) {
            mc.player.sendChatMessage(message);
        }
    }
    
    public void title(Object title, Object subtitle, int fadeIn, int remain, int fadeOut) {
        Text titlee = null;
        Text subtitlee = null;
        if (title instanceof TextHelper) titlee = ((TextHelper) title).getRaw();
        else if (title != null) titlee = new LiteralText(title.toString());
        if (subtitle instanceof TextHelper) subtitlee = ((TextHelper) subtitle).getRaw();
        else if (subtitle != null) subtitlee = new LiteralText(subtitle.toString());
        if (title != null)
            mc.inGameHud.setTitles(titlee, null, fadeIn, remain, fadeOut);
        if (subtitle != null)
            mc.inGameHud.setTitles(null, subtitlee, fadeIn, remain, fadeOut);
        if (title == null && subtitle == null)
            mc.inGameHud.setTitles(null, null, fadeIn, remain, fadeOut);
    }
    
    public void actionbar(Object text, boolean tinted) {
        Text textt = null;
        if (text instanceof TextHelper) textt = ((TextHelper) text).getRaw();
        else if (text != null) textt = new LiteralText(text.toString());
        mc.inGameHud.setOverlayMessage(textt, tinted);
    }
    
    public void toast(Object title, Object desc) {
        ToastManager t = mc.getToastManager();
        if (t != null) {
            Text titlee = (title instanceof TextHelper) ? ((TextHelper) title).getRaw() : title != null ? new LiteralText(title.toString()) : null;
            Text descc = (desc instanceof TextHelper) ? ((TextHelper) desc).getRaw() : desc != null ? new LiteralText(desc.toString()) : null;
            if (titlee != null) t.add(SystemToast.create(mc, null, titlee, descc));
        }
    }
    
    public TextHelper createTextHelperFromString(String content) {
        return new TextHelper(new LiteralText(content));
    }
    
    public TextHelper createTextHelperFromJSON(String json) {
        return new TextHelper(json);
    }
}
