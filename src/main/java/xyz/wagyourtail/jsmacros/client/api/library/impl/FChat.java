package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functions for interacting with chat.
 * <p>
 * An instance of this class is passed to scripts as the {@code Chat} variable.
 *
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public abstract class FChat extends BaseLibrary {
    protected final Supplier<DynamicRegistryManager> registryManagerSupplier;

    public FChat(Core<?, ?> runner, Supplier<DynamicRegistryManager> registryManagerSupplier) {
        super(runner);
        this.registryManagerSupplier = registryManagerSupplier;
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

        final Object message2 = switch (message) {
            case TextHelper text -> text;
            case TextBuilder builder -> builder.build();
            default -> message.toString();
        };

        if (message2 instanceof TextHelper) {
            logInternal((TextHelper) message2, await);
        } else {
            logInternal((String) message2, await);
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

    protected abstract void logInternal(String message, boolean await);

    protected abstract void logInternal(TextHelper text, boolean await);

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
        sayInternal(message);
    }

    protected abstract void sayInternal(String message);

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
     * Creates a {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper} for use where you need one and not a string.
     *
     * @param content
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper
     * @since 1.1.3
     */
    public TextHelper createTextHelperFromString(String content) {
        return TextHelper.wrap(Text.literal(content));
    }

    /**
     * @since 1.9.0
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
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
     * Create a  {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper} for use where you need one and not a string.
     *
     * @param json
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper TextHelper}
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper
     * @since 1.1.3
     */
    @Nullable
    public TextHelper createTextHelperFromJSON(String json) {
        TextHelper t = TextHelper.wrap(Text.Serialization.fromJson(json, registryManagerSupplier.get()));
        return t;
    }

    /**
     * @return a new builder
     * @since 1.3.0
     */
    public TextBuilder createTextBuilder() {
        return new TextBuilder();
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
