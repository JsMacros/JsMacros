package xyz.wagyourtail.jsmacros.stubs;

import org.slf4j.Logger;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
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
        BaseWrappedException e = Core.getInstance().wrapException(ex);
        LOGGER.error(e.message, ex);
    }

    @Override
    public boolean checkJoinedThreadStack() {
        return joinedThreadStack.contains(Thread.currentThread());
    }
}
