package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.access.ICommandNode;
import xyz.wagyourtail.jsmacros.client.api.classes.ChatHistoryManager;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
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
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            if (message instanceof TextHelper) {
                logInternal((TextHelper)message);
            } else if (message != null) {
                logInternal(message.toString());
            }
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                if (message instanceof TextHelper) {
                    logInternal((TextHelper) message);
                } else if (message != null) {
                    logInternal(message.toString());
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
            mc.setScreen(new ChatScreen(message));
        } else {
            String finalMessage = message;
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.setScreen(new ChatScreen(finalMessage));
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
            mc.inGameHud.setTitle(titlee);
        if (subtitle != null)
            mc.inGameHud.setSubtitle(subtitlee);
        if (title == null && subtitle == null) {
            mc.inGameHud.setTitle(null);
            mc.inGameHud.setSubtitle(null);
        }
        mc.inGameHud.setTitleTicks(fadeIn, remain, fadeOut);
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
        return LogManager.getLogger();
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

    /**
     * @param name name of command
     * @since 1.4.2
     * @return
     */
    public CommandBuilder createCommandBuilder(String name) {
        return CommandBuilder.createNewBuilder.apply(name);
    }

    /**
     * @param name
     * @since 1.6.5
     */
    public CommandNodeHelper unregisterCommand(String name) {
        CommandNodeHelper c = null;
        CommandNode<?> cnf = ((ICommandNode) ClientCommandManager.DISPATCHER.getRoot()).remove(name);
        CommandDispatcher<?> cd = MinecraftClient.getInstance().player.networkHandler.getCommandDispatcher();
        CommandNode<?> cn = ((ICommandNode) cd.getRoot()).remove(name);
        return cn != null || cnf != null ? new CommandNodeHelper(cn, cnf) : null;
    }

    /**
     * @since 1.6.5
     * @param node
     */
    public void reRegisterCommand(CommandNodeHelper node) {
        if (node.fabric != null) {
            ClientCommandManager.DISPATCHER.getRoot().addChild(node.fabric);
        }
        CommandDispatcher<?> cd = mc.player.networkHandler.getCommandDispatcher();
        if (node.getRaw() != null) {
            cd.getRoot().addChild((CommandNode) node.getRaw());
        }
    }

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
