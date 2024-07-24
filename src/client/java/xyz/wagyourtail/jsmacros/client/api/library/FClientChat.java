package xyz.wagyourtail.jsmacros.client.api.library;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.ChatHistoryManager;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.command.CommandManager;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FChat;
import xyz.wagyourtail.jsmacros.client.api.text.ClientTextBuilder;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.concurrent.Semaphore;

@Library("Chat")
public class FClientChat extends FChat {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FClientChat(Core<?, ?> runner) {
        super(runner, () -> mc.getNetworkHandler().getRegistryManager());
    }

    @Override
    protected void logInternal(String message, boolean await) {
        if (runner.profile.checkJoinedThreadStack()) {
            if (message != null) {
                Text text = Text.literal(message);
                ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
            }
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                if (message != null) {
                    Text text = Text.literal(message);
                    ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
                }
                semaphore.release();
            });
        }
    }

    @Override
    protected void logInternal(TextHelper text, boolean await) {
        if (runner.profile.checkJoinedThreadStack()) {
            ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text.getRaw());
        } else {
            final Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text.getRaw());
                semaphore.release();
            });
        }
    }

    @Override
    protected void sayInternal(String message) {
        if (runner.profile.checkJoinedThreadStack()) {
            if (message.startsWith("/")) {
                mc.getNetworkHandler().sendChatCommand(message.substring(1));
            } else {
                mc.getNetworkHandler().sendChatMessage(message);
            }
        } else {
            final Semaphore semaphore = new Semaphore(0);
            mc.execute(() -> {
                if (message.startsWith("/")) {
                    mc.getNetworkHandler().sendChatCommand(message.substring(1));
                } else {
                    mc.getNetworkHandler().sendChatMessage(message);
                }
                semaphore.release();
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
     * @return a new builder
     * @since 1.3.0
     */
    @Override
    public ClientTextBuilder createTextBuilder() {
        return new ClientTextBuilder();
    }

    /**
     * @return
     * @since 1.7.0
     */
    public ChatHistoryManager getHistory() {
        return new ChatHistoryManager(mc.inGameHud.getChatHud(), runner);
    }

    /**
     * @param text the text to get the width of
     * @return the width of the given text in pixels.
     * @since 1.8.4
     */
    public int getTextWidth(@Nullable String text) {
        return mc.textRenderer.getWidth(text);
    }

    /**
     * @param text the text to get the width of
     * @return the width of the given text in pixels.
     * @since 2.0.0
     */
    public int getTextWidth(TextHelper text) {
        return mc.textRenderer.getWidth(text.getRaw());
    }

    /**
     * @return
     * @since 1.7.0
     */
    public CommandManager getCommandManager() {
        return CommandManager.instance;
    }
}
