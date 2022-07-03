package xyz.wagyourtail.jsmacros.js.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.io.File;
import java.util.concurrent.PriorityBlockingQueue;

public class GraalScriptContext extends BaseScriptContext<Context> {
    public final PriorityBlockingQueue<WrappedThread> tasks = new PriorityBlockingQueue<>(11, (a, b) -> a.isRunning() ? -1000 : b.isRunning() ? 1000 : b.priority - a.priority);

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
        WrappedThread w = new WrappedThread(t, 5);
        w.setRunning();
        tasks.add(w);
    }

    @Override
    public void wrapSleep(SleepRunnable sleep) throws InterruptedException {
        getContext().leave();

        try {
            assert tasks.peek() != null;
            // remove self from queue
            int prio = tasks.poll().release();

            sleep.run();

            // put self at back of the queue
            tasks.add(new WrappedThread(Thread.currentThread(), prio));

            // wait to be at the front of the queue again
            WrappedThread joinable = tasks.peek();
            assert joinable != null;
            while (joinable.thread != Thread.currentThread()) {
                joinable.waitFor();
                joinable = tasks.peek();
                assert joinable != null;
            }
            tasks.peek().setRunning();
        } finally {
            getContext().enter();
        }
    }

}
