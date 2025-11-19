package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.screen.ChatHudLineHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 1.6.0
 */
public class ChatHistoryManager {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final ChatHud hud;

    public ChatHistoryManager(ChatHud hud) {
        this.hud = hud;
    }

    /**
     * @param index
     * @return
     * @since 1.6.0
     */
    public ChatHudLineHelper getRecvLine(int index) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            return new ChatHudLineHelper(hud.messages.get(index), hud);
        }
        ChatHudLineHelper[] helper = {null};
        final Semaphore semaphore = new Semaphore(0);
        mc.execute(() -> {
            helper[0] = new ChatHudLineHelper(hud.messages.get(index), hud);
            semaphore.release();
        });
        semaphore.acquire();
        return helper[0];
    }

    /**
     * @return the amount of messages in the chat history.
     * @throws InterruptedException
     * @since 1.8.4
     */
    public int getRecvCount() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(0);
        AtomicInteger count = new AtomicInteger(0);
        mc.execute(() -> {
            count.set(hud.messages.size());
            semaphore.release();
        });
        semaphore.acquire();
        return count.get();
    }

    /**
     * @return all received messages in the chat history.
     * @throws InterruptedException
     * @since 1.8.4
     */
    public List<ChatHudLineHelper> getRecvLines() throws InterruptedException {
        List<ChatHudLineHelper> recvLines = new ArrayList<>();
        final Semaphore semaphore = new Semaphore(0);
        mc.execute(() -> {
            hud.messages.stream().map(textChatHudLine -> new ChatHudLineHelper(textChatHudLine, hud)).forEach(recvLines::add);
            semaphore.release();
        });
        semaphore.acquire();
        return recvLines;
    }

    /**
     * @param index
     * @param line
     * @since 1.6.0
     */
    public void insertRecvText(int index, TextHelper line) throws InterruptedException {
        insertRecvText(index, line, 0, false);
    }

    /**
     * you should probably run {@link #refreshVisible()} after...
     *
     * @param index
     * @param line
     * @param timeTicks
     * @since 1.6.0
     */
    public void insertRecvText(int index, TextHelper line, int timeTicks) throws InterruptedException {
        insertRecvText(index, line, timeTicks, false);
    }

    /**
     * @param index
     * @param line
     * @param timeTicks
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void insertRecvText(int index, TextHelper line, int timeTicks, boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            ((IChatHud) hud).jsmacros_addMessageAtIndexBypass(line.getRaw(), index, timeTicks);
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            ((IChatHud) hud).jsmacros_addMessageAtIndexBypass(line.getRaw(), index, timeTicks);
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @param index
     * @since 1.6.0
     */
    public void removeRecvText(int index) throws InterruptedException {
        removeRecvText(index, false);
    }

    /**
     * @param index
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void removeRecvText(int index, boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            hud.messages.remove(index);
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            hud.messages.remove(index);
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @param text
     * @since 1.6.0
     */
    public void removeRecvTextMatching(TextHelper text) throws InterruptedException {
        removeRecvTextMatching(text, false);
    }

    /**
     * @param text
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void removeRecvTextMatching(TextHelper text, boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            hud.messages.removeIf(c -> c.content().equals(text.getRaw()));
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            hud.messages.removeIf(c -> c.content().equals(text.getRaw()));
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @param filter
     * @since 1.6.0
     */
    public void removeRecvTextMatchingFilter(MethodWrapper<ChatHudLineHelper, Object, Boolean, ?> filter) throws InterruptedException {
        removeRecvTextMatchingFilter(filter, false);
    }

    /**
     * @param filter
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void removeRecvTextMatchingFilter(MethodWrapper<ChatHudLineHelper, Object, Boolean, ?> filter, boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            hud.messages.removeIf((c) -> filter.test(new ChatHudLineHelper(c, hud)));
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        Throwable[] ex = new Throwable[]{null};
        mc.execute(() -> {
            try {
                hud.messages.removeIf((c) -> filter.test(new ChatHudLineHelper(c, hud)));
            } catch (Throwable e) {
                ex[0] = e;
                if (!await && !(e.getCause() instanceof InterruptedException)) {
                    JsMacrosClient.clientCore.profile.logError(e);
                }
            }
            semaphore.release();
        });
        semaphore.acquire();
        if (ex[0] != null) {
            throw new RuntimeException(ex[0]);
        }
    }

    /**
     * this will reset the view of visible messages
     *
     * @since 1.6.0
     */
    public void refreshVisible() throws InterruptedException {
        refreshVisible(false);
    }

    /**
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void refreshVisible(boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            hud.reset();
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            hud.reset();
            semaphore.release();
        });
        semaphore.acquire();

    }

    /**
     * @since 1.6.0
     */
    public void clearRecv() throws InterruptedException {
        clearRecv(false);
    }

    /**
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void clearRecv(boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            hud.clear(false);
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            hud.clear(false);
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @return direct reference to sent message history list. modifications will affect the list.
     * @since 1.6.0
     */
    public List<String> getSent() {
        return hud.getMessageHistory();
    }

    /**
     * @since 1.6.0
     */
    public void clearSent() throws InterruptedException {
        clearSent(false);
    }

    /**
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public void clearSent(boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            hud.getMessageHistory().clear();
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            hud.getMessageHistory().clear();
            semaphore.release();
        });
        semaphore.acquire();
    }

}
