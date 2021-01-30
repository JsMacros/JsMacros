package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.concurrent.Semaphore;

/**
 * Functions for interacting with chat.
 * 
 * An instance of this class is passed to scripts as the {@code chat} variable.
 * 
 * @author Wagyourtail
 */
 @Library("chat")
 @SuppressWarnings("unused")
public class FChat extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Log to player chat.
     * 
     * @since 1.1.3
     * 
     * @param message
     */
    public void log(Object message) throws InterruptedException {
        log(message, false);
    }
    
    /**
     * @param message
     * @param await should wait for message to actually be sent to chat to continue.
     *
     * @throws InterruptedException
     */
    public void log(Object message, boolean await) throws InterruptedException {
        final Semaphore semaphore = new Semaphore(0);
        mc.execute(() -> {
            if (message instanceof TextHelper) {
                logInternal((TextHelper)message);
            } else if (message != null) {
                logInternal(message.toString());
            }
            semaphore.release();
        });
        semaphore.acquire();
    }
    
    private static void logInternal(String message) {
        if (message != null) {
            LiteralText text = new LiteralText(message);
            ((IChatHud)mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
        }
    }
    
    private static void logInternal(TextHelper text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ((IChatHud)mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text.getRaw());
    }
    
    /**
     * Say to server as player.
     * 
     * @since 1.0.0
     * 
     * @param message
     */
    public void say(String message) {
        if (message != null) {
            assert mc.player != null;
            mc.player.sendChatMessage(message);
        }
    }
    
    /**
     * Display a Title to the player.
     * 
     * @since 1.2.1
     * 
     * @param title
     * @param subtitle
     * @param fadeIn
     * @param remain
     * @param fadeOut
     */
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
    
    /**
     * Display the smaller title that's above the actionbar.
     * 
     * @since 1.2.1
     * 
     * @param text
     * @param tinted
     */
    public void actionbar(Object text, boolean tinted) {
        assert mc.inGameHud != null;
        Text textt = null;
        if (text instanceof TextHelper) textt = ((TextHelper) text).getRaw();
        else if (text != null) textt = new LiteralText(text.toString());
        mc.inGameHud.setOverlayMessage(textt, tinted);
    }
    
    /**
     * Display a toast.
     * 
     * @since 1.2.5
     * 
     * @param title
     * @param desc
     */
    public void toast(Object title, Object desc) {
        ToastManager t = mc.getToastManager();
        if (t != null) {
            Text titlee = (title instanceof TextHelper) ? ((TextHelper) title).getRaw() : title != null ? new LiteralText(title.toString()) : null;
            Text descc = (desc instanceof TextHelper) ? ((TextHelper) desc).getRaw() : desc != null ? new LiteralText(desc.toString()) : null;
            if (titlee != null) t.add(SystemToast.create(mc, null, titlee, descc));
        }
    }
    
    /**
     * Creates a {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper} for use where you need one and not a string.
     * 
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper
     * @since 1.1.3
     * 
     * @param content
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
     */
    public TextHelper createTextHelperFromString(String content) {
        return new TextHelper(new LiteralText(content));
    }
    
    /**
     * Create a  {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper} for use where you need one and not a string.
     * 
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper
     * @since 1.1.3
     * 
     * @param json
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
     */
    public TextHelper createTextHelperFromJSON(String json) {
        return new TextHelper(json);
    }
    
    /**
     * @see TextBuilder
     * @since 1.3.0
     * @return a new builder
     */
    public TextBuilder createTextBuilder() {
        return new TextBuilder();
    }
}
