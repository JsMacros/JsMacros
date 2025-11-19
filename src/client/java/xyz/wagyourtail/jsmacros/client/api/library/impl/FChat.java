package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.ChatHistoryManager;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.CommandManager;
import xyz.wagyourtail.jsmacros.client.api.helper.CommandNodeHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functions for interacting with chat.
 * <p>
 * An instance of this class is passed to scripts as the {@code Chat} variable.
 *
 * @author Wagyourtail
 */
@Library("Chat")
@SuppressWarnings("unused")
public class FChat extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FChat(Core<?, ?> runner) {
        super(runner);
    }

    /**
     * Log to player chat.
     *
     * @param message
     * @since 1.1.3
     */
    public void log(@Nullable Object message) throws InterruptedException {
        log(message, false);
    }

    /**
     * @param message
     * @param await   should wait for message to actually be sent to chat to continue.
     * @throws InterruptedException
     */
    public void log(@Nullable Object message, boolean await) throws InterruptedException {
        if (message == null) {
            return;
        }
        final Object message2 = message instanceof TextHelper ? message :
                message instanceof TextBuilder ? ((TextBuilder) message).build() :
                        message.toString();

        if (runner.profile.checkJoinedThreadStack()) {
            if (message2 instanceof TextHelper) {
                logInternal((TextHelper) message2);
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

    /**
     * Logs the formatted message to the player's chat. The message is formatted using the default
     * java {@link String#format(String, Object...)} syntax.
     *
     * @param message the message to format and log
     * @param args    the arguments used to format the message
     * @throws InterruptedException
     * @since 1.8.4
     */
    public void logf(String message, Object... args) throws InterruptedException {
        log(String.format(message, args), false);
    }

    /**
     * Logs the formatted message to the player's chat. The message is formatted using the default
     * java {@link String#format(String, Object...)} syntax.
     *
     * @param message the message to format and log
     * @param await   whether to wait for message to be sent to chat before continuing
     * @param args    the arguments used to format the message
     * @throws InterruptedException
     * @since 1.8.4
     */
    public void logf(String message, boolean await, Object... args) throws InterruptedException {
        log(String.format(message, args), await);
    }

    /**
     * log with auto wrapping with {@link #ampersandToSectionSymbol(String)}
     *
     * @since 1.9.0
     * @param message
     * @throws InterruptedException
     */
    public void logColor(String message) throws InterruptedException {
        log(ampersandToSectionSymbol(message), false);
    }

    /**
     * log with auto wrapping with {@link #ampersandToSectionSymbol(String)}
     *
     * @since 1.9.0
     * @param message
     * @param await
     * @throws InterruptedException
     */
    public void logColor(String message, boolean await) throws InterruptedException {
        log(ampersandToSectionSymbol(message), await);
    }

    private static void logInternal(String message) {
        if (message != null) {
            Text text = Text.literal(message);
            ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
        }
    }

    private static void logInternal(TextHelper text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text.getRaw());
    }

    /**
     * Say to server as player.
     *
     * @param message
     * @since 1.0.0
     */
    public void say(@Nullable String message) throws InterruptedException {
        say(message, false);
    }

    /**
     * Say to server as player.
     *
     * @param message
     * @param await
     * @throws InterruptedException
     * @since 1.3.1
     */
    public void say(@Nullable String message, boolean await) throws InterruptedException {
        if (message == null) {
            return;
        }
        if (runner.profile.checkJoinedThreadStack()) {
            assert mc.player != null;
            sayInternal(message);
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                assert mc.player != null;
                sayInternal(message);
                semaphore.release();
            });
            semaphore.acquire();
        }
    }

    private void sayInternal(String message) {
        if (message.startsWith("/")) {
            mc.getNetworkHandler().sendChatCommand(message.substring(1));
        } else {
            mc.getNetworkHandler().sendChatMessage(message);
        }
    }

    /**
     * Sends the formatted message to the server. The message is formatted using the default java
     * {@link String#format(String, Object...)} syntax.
     *
     * @param message the message to format and send to the server
     * @param args    the arguments used to format the message
     * @throws InterruptedException
     * @since 1.8.4
     */
    public void sayf(String message, Object... args) throws InterruptedException {
        say(String.format(message, args), false);
    }

    /**
     * Sends the formatted message to the server. The message is formatted using the default java
     * {@link String#format(String, Object...)} syntax.
     *
     * @param message the message to format and send to the server
     * @param await   whether to wait for message to be sent to chat before continuing
     * @param args    the arguments used to format the message
     * @throws InterruptedException
     * @since 1.8.4
     */
    public void sayf(String message, boolean await, Object... args) throws InterruptedException {
        say(String.format(message, args), await);
    }

    /**
     * open the chat input box with specific text already typed.
     *
     * @param message the message to start the chat screen with
     * @since 1.6.4
     */
    public void open(@Nullable String message) throws InterruptedException {
        open(message, false);
    }

    /**
     * open the chat input box with specific text already typed.
     * hint: you can combine with {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#waitForEvent(String)} or
     * {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#once(String, MethodWrapper)} to wait for the chat screen
     * to close and/or the to wait for the sent message
     *
     * @param message the message to start the chat screen with
     * @param await
     * @since 1.6.4
     */
    public void open(@Nullable String message, boolean await) throws InterruptedException {
        if (message == null) {
            message = "";
        }
        if (runner.profile.checkJoinedThreadStack()) {
            throw new UnsupportedOperationException("Cannot open a screen while joined to the main thread");
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
     * @param title
     * @param subtitle
     * @param fadeIn
     * @param remain
     * @param fadeOut
     * @since 1.2.1
     */
    public void title(Object title, Object subtitle, int fadeIn, int remain, int fadeOut) {
        Text titlee = null;
        Text subtitlee = null;
        if (title instanceof TextHelper) {
            titlee = ((TextHelper) title).getRaw();
        } else if (title != null) {
            titlee = Text.literal(title.toString());
        }
        if (subtitle instanceof TextHelper) {
            subtitlee = ((TextHelper) subtitle).getRaw();
        } else if (subtitle != null) {
            subtitlee = Text.literal(subtitle.toString());
        }
        if (title != null) {
            mc.inGameHud.setTitle(titlee);
        }
        if (subtitle != null) {
            mc.inGameHud.setSubtitle(subtitlee);
        }
        if (title == null && subtitle == null) {
            mc.inGameHud.setTitle(null);
            mc.inGameHud.setSubtitle(null);
        }
        mc.inGameHud.setTitleTicks(fadeIn, remain, fadeOut);
    }

    /**
     * @param text
     * @since 1.8.1
     */
    public void actionbar(Object text) {
        actionbar(text, false);
    }

    /**
     * Display the smaller title that's above the actionbar.
     *
     * @param text
     * @param tinted
     * @since 1.2.1
     */
    public void actionbar(Object text, boolean tinted) {
        assert mc.inGameHud != null;
        Text textt = null;
        if (text instanceof TextHelper) {
            textt = ((TextHelper) text).getRaw();
        } else if (text != null) {
            textt = Text.literal(text.toString());
        }
        mc.inGameHud.setOverlayMessage(textt, tinted);
    }

    /**
     * Display a toast.
     *
     * @param title
     * @param desc
     * @since 1.2.5
     */
    public void toast(Object title, Object desc) {
        ToastManager t = mc.getToastManager();
        if (t != null) {
            Text titlee = (title instanceof TextHelper) ? ((TextHelper) title).getRaw() : title != null ? Text.literal(title.toString()) : null;
            Text descc = (desc instanceof TextHelper) ? ((TextHelper) desc).getRaw() : desc != null ? Text.literal(desc.toString()) : null;
            // There doesn't seem to be a difference in the appearance or the functionality except for the UNSECURE_SERVER_WARNING with a longer duration
            if (titlee != null) {
                t.add(SystemToast.create(mc, SystemToast.Type.PERIODIC_NOTIFICATION, titlee, descc));
            }
        }
    }

    /**
     * Creates a {@link TextHelper TextHelper} for use where you need one and not a string.
     *
     * @param content
     * @return a new {@link TextHelper TextHelper}
     * @see TextHelper
     * @since 1.1.3
     */
    public TextHelper createTextHelperFromString(String content) {
        return TextHelper.wrap(Text.literal(content));
    }

    /**
     * @since 1.9.0
     * @return a new {@link TextHelper TextHelper}
     */
    public TextHelper createTextHelperFromTranslationKey(String key, Object... content) {
        return TextHelper.wrap(Text.translatable(key, content));
    }

    /**
     * @return
     * @since 1.5.2
     */
    public Logger getLogger() {
        return JsMacros.LOGGER;
    }

    /**
     * returns a log4j logger, for logging to console only.
     *
     * @param name
     * @return
     * @since 1.5.2
     */
    public Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    /**
     * Create a  {@link TextHelper TextHelper} for use where you need one and not a string.
     *
     * @param json
     * @return a new {@link TextHelper TextHelper}
     * @see TextHelper
     * @since 1.1.3
     */
    @Nullable
    public TextHelper createTextHelperFromJSON(String json) {
        Text s = Text.of(json);
        TextHelper t = TextHelper.wrap(s);
        return t;
    }

    /**
     * @return a new builder
     * @see TextBuilder
     * @since 1.3.0
     */
    public TextBuilder createTextBuilder() {
        return new TextBuilder();
    }

    /**
     * @param name name of command
     * @return
     * @see #getCommandManager()
     * @since 1.4.2
     */
    @Deprecated
    public CommandBuilder createCommandBuilder(String name) {
        return CommandManager.instance.createCommandBuilder(name);
    }

    /**
     * @param name
     * @see #getCommandManager()
     * @since 1.6.5
     */
    @Deprecated
    public CommandNodeHelper unregisterCommand(String name) throws IllegalAccessException {
        return CommandManager.instance.unregisterCommand(name);
    }

    /**
     * @param node
     * @see #getCommandManager()
     * @since 1.6.5
     */
    @Deprecated
    public void reRegisterCommand(CommandNodeHelper node) {
        CommandManager.instance.reRegisterCommand(node);
    }

    /**
     * @return
     * @since 1.7.0
     */
    public CommandManager getCommandManager() {
        return CommandManager.instance;
    }

    /**
     * @return
     * @since 1.7.0
     */
    public ChatHistoryManager getHistory() {
        return new ChatHistoryManager(mc.inGameHud.getChatHud());
    }

    /**
     * @param text the text to get the width of
     * @return the width of the given text in pixels.
     * @since 1.8.4
     */
    public int getTextWidth(@Nullable String text) {
        return mc.textRenderer.getWidth(text);
    }

    private static final Pattern SECTION_SYMBOL_PATTERN = Pattern.compile("[§&]");

    /**
     * escapes &amp; to &amp;&amp; since 1.9.0
     * @param string
     * @return &#167; -> &amp;
     * @since 1.6.5
     */
    public String sectionSymbolToAmpersand(String string) {
        StringBuilder sb = new StringBuilder();
        Matcher m = SECTION_SYMBOL_PATTERN.matcher(string);
        while (m.find()) {
            if (m.group().equals("§"))
                m.appendReplacement(sb, "&");
            else
                m.appendReplacement(sb, "&&");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("&(.)");

    /**
     * escapes &amp;&amp; to &amp; since 1.9.0
     * @param string
     * @return &amp; -> &#167;
     * @since 1.6.5
     */
    public String ampersandToSectionSymbol(String string) {
        StringBuilder sb = new StringBuilder();
        Matcher m = AMPERSAND_PATTERN.matcher(string);
        while (m.find()) {
            if (m.group().equals("&&"))
                m.appendReplacement(sb, "&");
            else
                m.appendReplacement(sb, "§$1");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param string
     * @return
     * @since 1.6.5
     */
    public String stripFormatting(String string) {
        // on 1.15 and lower switch to comment
//        return string.replaceAll("§#\\d{6}|§.", "");
        return TextHelper.STRIP_FORMATTING_PATTERN.matcher(string).replaceAll("");
    }

}
