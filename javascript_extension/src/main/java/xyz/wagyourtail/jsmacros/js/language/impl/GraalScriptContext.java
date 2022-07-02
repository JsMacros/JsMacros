package xyz.wagyourtail.jsmacros.js.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.js.library.impl.FWrapper;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class GraalScriptContext extends BaseScriptContext<Context> {
    public final LinkedBlockingQueue<FWrapper.WrappedThread> tasks = new LinkedBlockingQueue<>();

    public GraalScriptContext(BaseEvent event, File file) {
        super(event, file);
    }
    
    @Override
    public void closeContext() {
        super.closeContext();
        Context ctx = getContext();
        if (ctx != null) {
            ctx.close(true);
        }
    }

    @Override
    public boolean isMultiThreaded() {
        return false;
    }

    @Override
    public void setMainThread(Thread t) {
        super.setMainThread(t);
        try {
            tasks.put(new FWrapper.WrappedThread(t, true));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void wrapSleep(SleepRunnable sleep) throws InterruptedException {
        getContext().leave();

        try {
            assert tasks.peek() != null;
            // remove self from queue
            tasks.poll().release();

            sleep.run();

            // put self at back of the queue
            tasks.put(new FWrapper.WrappedThread(Thread.currentThread(), true));

            // wait to be at the front of the queue again
            FWrapper.WrappedThread joinable = tasks.peek();
            assert joinable != null;
            while (joinable.thread != Thread.currentThread()) {
                joinable.waitFor();
                joinable = tasks.peek();
                assert joinable != null;
            }
        } finally {
            getContext().enter();
        }
    }

}
