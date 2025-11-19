package xyz.wagyourtail.jsmacros.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

public class EventLockWatchdog {

    public static void startWatchdog(@NotNull EventContainer<?> lock, @Nullable IEventListener listener, long maxTime) {
        lock.getCtx().runner.threadPool.runTask(() -> {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(maxTime);
                    synchronized (lock) {
                        if (!lock.isLocked()) {
                            return;
                        }
                    }
                    lock.getCtx().closeContext();
                    lock.releaseLock();
                    if (listener instanceof BaseListener) {
                        ((BaseListener) listener).getRawTrigger().enabled = false;
                    }
                    lock.getCtx().runner.profile.logError(new WatchdogException(String.format("Script \n\"%s\"\n joined longer than allowed time of %d ms.", listener, maxTime)));
                } catch (InterruptedException ignored) {
                }
            });
            Thread u = new Thread(() -> {
                try {
                    lock.awaitLock(() -> {
                        synchronized (lock) {
                            t.interrupt();
                        }
                    });
                } catch (InterruptedException ignored) {
                }
            });
            t.setPriority(Thread.NORM_PRIORITY - 1);
            u.setPriority(Thread.NORM_PRIORITY - 1);
            t.start();
            u.start();
        });
    }

    private static class WatchdogException extends RuntimeException {
        public WatchdogException(String message) {
            super(message);
        }

    }

}
