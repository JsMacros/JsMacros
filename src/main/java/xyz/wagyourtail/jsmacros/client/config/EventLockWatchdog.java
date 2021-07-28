package xyz.wagyourtail.jsmacros.client.config;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

public class EventLockWatchdog {
    public static void startWatchdog(EventContainer<?> lock, IEventListener listener, long maxTime) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(maxTime);
                lock.getCtx().closeContext();
                if (listener instanceof BaseListener) {
                    ((BaseListener) listener).getRawTrigger().enabled = false;
                }
                WatchdogException ex = new WatchdogException(String.format("Script \n\"%s\"\n joined longer than allowed time of %d ms.", listener.toString(), maxTime));
                Core.instance.profile.logError(ex);
                ex.printStackTrace();
            } catch (InterruptedException ignored) { }
        });
        Thread u = new Thread(() -> {
            try {
                lock.awaitLock(t::interrupt);
            } catch (InterruptedException ignored) {}
        });
        t.start();
        u.start();
    }

    private static class WatchdogException extends RuntimeException {
        public WatchdogException(String message) {
            super(message);
        }
    }
}

