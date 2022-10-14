package xyz.wagyourtail.jsmacros.js.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.PrioryFiFoTaskQueue;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.io.File;

public class GraalScriptContext extends BaseScriptContext<Context> {
    public final PrioryFiFoTaskQueue<WrappedThread> tasks = new PrioryFiFoTaskQueue<>(GraalScriptContext::getThreadPriority);
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

    public static int getThreadPriority(Object thread) {
        return -((WrappedThread) thread).priority;
    }
    @Override
    public boolean isMultiThreaded() {
        return false;
    }

    @Override
    public void setMainThread(Thread t) {
        super.setMainThread(t);
        WrappedThread w = new WrappedThread(t, 5);
        tasks.add(w);
    }

    @Override
    public void wrapSleep(SleepRunnable sleep) throws InterruptedException {
        wrapSleep(0, sleep);
    }

    public void wrapSleep(int changePriority, SleepRunnable sleep) throws InterruptedException {
        getContext().leave();

        assert tasks.peek() != null;
        // remove self from queue
        int prio = tasks.poll().release();

        try {
            sleep.run();
        } finally {
            // put self at back of the queue
            tasks.add(new WrappedThread(Thread.currentThread(), prio + changePriority));

            // wait to be at the front of the queue again
            WrappedThread joinable = tasks.peek();
            assert joinable != null;
            while (joinable.thread != Thread.currentThread()) {
                joinable.waitFor();
                joinable = tasks.peek();
                assert joinable != null;
            }

            getContext().enter();
        }
    }
}
