package xyz.wagyourtail.jsmacros.stubs;

import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros;

import java.util.concurrent.LinkedBlockingQueue;

public class ProfileStub extends BaseProfile {

    static Thread th;
    static LinkedBlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>();

    static {
        th = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    runnables.take().run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

    public ProfileStub(Core<?, ?> runner, Logger logger) {
        super(runner, logger);
        joinedThreadStack.add(th);
        initRegistries();
    }

    @Override
    public void logError(Throwable ex) {
        LOGGER.error("", ex);
    }

    @Override
    public boolean checkJoinedThreadStack() {
        return joinedThreadStack.contains(Thread.currentThread());
    }

    @Override
    public void triggerEventJoin(BaseEvent event) {
        boolean joinedMain = checkJoinedThreadStack();
        triggerEventJoinNoAnything(event);

        for (IEventListener macro : runner.eventRegistry.getListeners("ANYTHING")) {
            runJoinedEventListener(event, joinedMain, macro);
        }
    }

    @Override
    public void triggerEventJoinNoAnything(BaseEvent event) {

        boolean joinedMain = checkJoinedThreadStack();
        if (event instanceof EventCustom) {
            for (IEventListener macro : runner.eventRegistry.getListeners(((EventCustom) event).eventName)) {
                runJoinedEventListener(event, joinedMain, macro);
            }
        } else {
            for (IEventListener macro : runner.eventRegistry.getListeners(event.getEventName())) {
                runJoinedEventListener(event, joinedMain, macro);
            }
        }
    }


    private void runJoinedEventListener(BaseEvent event, boolean joinedMain, IEventListener macroListener) {
        if (macroListener instanceof FJsMacros.ScriptEventListener && ((FJsMacros.ScriptEventListener) macroListener).getCreator() == Thread.currentThread() && ((FJsMacros.ScriptEventListener) macroListener).getWrapper().preventSameThreadJoin()) {
            throw new IllegalThreadStateException("Cannot join " + macroListener + " on same thread as it's creation.");
        }
        EventContainer<?> t = macroListener.trigger(event);
        if (t == null) return;
        try {
            if (joinedMain) {
                joinedThreadStack.add(t.getLockThread());
                EventLockWatchdog.startWatchdog(t, macroListener, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
            }
            t.awaitLock(() -> joinedThreadStack.remove(t.getLockThread()));
        } catch (InterruptedException ignored) {
            joinedThreadStack.remove(t.getLockThread());
        }
    }

}
