package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.classes.ChatHistoryManager;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandManager;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandNodeHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.concurrent.Semaphore;

/**
 * Functions for interacting with chat.
 * 
 * An instance of this class is passed to scripts as the {@code Chat} variable.
 * 
 * @author Wagyourtail
 */
 @Library("Chat")
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
        if (message == null) return;
        final Object message2 = message instanceof TextHelper ? message :
            message instanceof TextBuilder ? ((TextBuilder) message).build() :
                message.toString();

        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            if (message2 instanceof TextHelper) {
                logInternal((TextHelper)message2);
            } else {
                logInternal((String) message2);
            }
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                if (message2 instanceof TextHelper) {
                    logInternal((TextHelper) message2);
                } else {
                    logInternal((String) message2);
                }
                semaphore.release();
            });
            semaphore.acquire();
        }
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
     public void say(String message) throws InterruptedException {
        say(message, false);
     }
    
    /**
    * Say to server as player.
    *
     * @param message
     * @param await
     * @since 1.3.1
     *
     * @throws InterruptedException
     */
    public void say(String message, boolean await) throws InterruptedException {
        if (message == null) return;
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            assert mc.player != null;
            mc.player.sendChatMessage(message);
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                assert mc.player != null;
                mc.player.sendChatMessage(message);
                semaphore.release();
            });
            semaphore.acquire();
        }
    }

    /**
     * open the chat input box with specific text already typed.
     *
     * @since 1.6.4
     * @param message the message to start the chat screen with
     */
     public void open(String message) throws InterruptedException {
        open(message, false);
     }

    /**
     * open the chat input box with specific text already typed.
     * hint: you can combine with {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#waitForEvent(String)} or
     * {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#once(String, MethodWrapper)} to wait for the chat screen
     * to close and/or the to wait for the sent message
     *
     * @since 1.6.4
     * @param message the message to start the chat screen with
     * @param await
     */
    public void open(String message, boolean await) throws InterruptedException {
        if (message == null) message = "";
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            throw new UnsupportedOperationException("Cannot open a screen while joined to the main thread");
        } else {
            String finalMessage = message;
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.openScreen(new ChatScreen(finalMessage));
                semaphore.release();
            });
            semaphore.acquire();
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
     * @since 1.8.1
     * @param text
     */
    public void actionbar(Object text) {
        actionbar(text, false);
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
     * @since 1.5.2
     * @return
     */
    public Logger getLogger() {
        return JsMacros.LOGGER;
    }

    /**
     * returns a log4j logger, for logging to console only.
     * @since 1.5.2
     * @param name
     * @return
     */
    public Logger getLogger(String name) {
        return LogManager.getLogger(name);
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
        TextHelper t = new TextHelper(null);
        t.replaceFromJson(json);
        return t;
    }
    
    /**
     * @see TextBuilder
     * @since 1.3.0
     * @return a new builder
     */
    public TextBuilder createTextBuilder() {
        return new TextBuilder();
    }

    /**
     *
     *@param name name of command
     * @since 1.4.2
     * @return
     * @see #getCommandManager()
     */
     @Deprecated
    public CommandBuilder createCommandBuilder(String name) {
        return CommandManager.instance.createCommandBuilder(name);
    }

    /**
     * @param name
     * @since 1.6.5
     * @see #getCommandManager()
     */
     @Deprecated
    public CommandNodeHelper unregisterCommand(String name) throws IllegalAccessException {
        return CommandManager.instance.unregisterCommand(name);
    }

    /**
     * @since 1.6.5
     * @param node
     * @see #getCommandManager()
     */
     @Deprecated
    public void reRegisterCommand(CommandNodeHelper node) {
        CommandManager.instance.reRegisterCommand(node);
    }

    /**
     * @since 1.7.0
     * @return
     */
    public CommandManager getCommandManager() {
        return CommandManager.instance;
    }

    /**
     * @since 1.7.0
     * @return
     */
    public ChatHistoryManager getHistory() {
        return new ChatHistoryManager(mc.inGameHud.getChatHud());
    }

    /**
     * @param string
     * @since 1.6.5
     * @return &#167; -> &amp;
     */
    public String sectionSymbolToAmpersand(String string) {
        return string.replaceAll("§", "&");
    }

    /**
     * @param string
     * @since 1.6.5
     * @return &amp; -> &#167;
     */
    public String ampersandToSectionSymbol(String string) {
        return string.replaceAll("&", "§");
    }

    /**
     * @param string
     * @since 1.6.5
     * @return
     */
     public String stripFormatting(String string) {
     // on 1.15 and lower switch to comment
//        return string.replaceAll("§#\\d{6}|§.", "");
        return string.replaceAll("§.", "");
    }
}
