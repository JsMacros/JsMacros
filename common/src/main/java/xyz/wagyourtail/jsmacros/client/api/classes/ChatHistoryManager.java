package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.helpers.ChatHudLineHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @since 1.6.0
 */
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
     * @since 1.6.0
     * @return
     */
    public ChatHudLineHelper getRecvLine(int index) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            return new ChatHudLineHelper(((IChatHud) hud).jsmacros_getMessages().get(index), hud);
        }
        ChatHudLineHelper[] helper = {null};
        final Semaphore semaphore = new Semaphore(0);
        mc.execute(() -> {
            helper[0] = new ChatHudLineHelper(((IChatHud) hud).jsmacros_getMessages().get(index), hud);
            semaphore.release();
        });
        semaphore.acquire();
        return helper[0];
    }

    /**
     * @since 1.6.0
     * @param index
     * @param line
     */
    public void insertRecvText(int index, TextHelper line) throws InterruptedException {
        insertRecvText(index, line, 0, false);
    }

    /**
     * you should probably run {@link #refreshVisible()} after...
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
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void insertRecvText(int index, TextHelper line, int timeTicks, boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
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
     * @since 1.6.0
     * @param index
     */
    public void removeRecvText(int index) throws InterruptedException {
        removeRecvText(index, false);
    }

    /**
     * @param index
     * @param await
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void removeRecvText(int index, boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            ((IChatHud) hud).jsmacros_removeMessage(index);
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            ((IChatHud) hud).jsmacros_removeMessage(index);
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @since 1.6.0
     * @param text
     */
    public void removeRecvTextMatching(TextHelper text) throws InterruptedException {
        removeRecvTextMatching(text, false);
    }

    /**
     * @param text
     * @param await
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void removeRecvTextMatching(TextHelper text, boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            ((IChatHud) hud).jsmacros_removeMessageByText(text.getRaw());
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            ((IChatHud) hud).jsmacros_removeMessageByText(text.getRaw());
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @since 1.6.0
     * @param filter
     */
    public void removeRecvTextMatchingFilter(MethodWrapper<ChatHudLineHelper, Object, Boolean, ?> filter) throws InterruptedException {
        removeRecvTextMatchingFilter(filter, false);
    }

    /**
     * @param filter
     * @param await
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void removeRecvTextMatchingFilter(MethodWrapper<ChatHudLineHelper, Object, Boolean, ?> filter, boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            ((IChatHud) hud).jsmacros_removeMessagePredicate((c) -> filter.test(new ChatHudLineHelper(c, hud)));
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        Throwable[] ex = new Throwable[] {null};
        mc.execute(() -> {
            try {
                ((IChatHud) hud).jsmacros_removeMessagePredicate((c) -> filter.test(new ChatHudLineHelper(c, hud)));
            } catch (Throwable e) {
                ex[0] = e;
                if (!await && !(e.getCause() instanceof InterruptedException)) {
                    Core.getInstance().profile.logError(e);
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
     * @since 1.6.0
     */
    public void refreshVisible() throws InterruptedException {
        refreshVisible(false);
    }

    /**
     * @param await
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void refreshVisible(boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
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
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void clearRecv(boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            ((IChatHud) hud).jsmacros_getMessages().clear();
            hud.reset();
            return;
        }
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            ((IChatHud) hud).jsmacros_getMessages().clear();
            hud.reset();
            semaphore.release();
        });
        semaphore.acquire();
    }

    /**
     * @since 1.6.0
     * @return direct reference to sent message history list. modifications will affect the list.
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
     * @since 1.6.0
     * @throws InterruptedException
     */
    public void clearSent(boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
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