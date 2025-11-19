package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 * @since 1.4.0
 */
public class EventContainer<T extends BaseScriptContext<?>> {
    private final T ctx;
    private Thread lockThread;
    private boolean locked = true;

    private final List<Runnable> then = new ArrayList<>();

    public EventContainer(T ctx) {
        this.ctx = ctx;
    }

    public synchronized boolean isLocked() {
        return locked;
    }

    public synchronized void setLockThread(Thread lockThread) {
        if (this.lockThread != null) {
            throw new AssertionError("Cannot change lock thread of context container once assigned!");
        }
        this.lockThread = lockThread;
        if (locked && ctx != null)
            ctx.events.put(lockThread, (EventContainer) this);
    }

    public T getCtx() {
        return ctx;
    }

    public Thread getLockThread() {
        return lockThread;
    }

    /**
     * careful with this one it can cause deadlocks if used in scripts incorrectly.
     *
     * @param then must be a {@link MethodWrapper} when called from a script.
     * @throws InterruptedException
     * @since 1.4.0
     */
    public synchronized void awaitLock(Runnable then) throws InterruptedException {
        if (ctx != null && ctx.threads.contains(Thread.currentThread())) {
            if (then != null && !(then instanceof MethodWrapper)) {
                throw new AssertionError("For your safety, please use MethodWrapper in scripts.");
            }
        }
        if (locked) {
            if (then != null) {
                this.then.add(then);
            }
            this.wait();
        } else {
            try {
                then.run();
            } catch (Throwable t) {
                ctx.runner.profile.logError(t);
            }
        }
    }

    /**
     * can be released earlier in a script or language impl.
     *
     * @since 1.4.0
     */
    public synchronized void releaseLock() {
        locked = false;
        ctx.runner.profile.joinedThreadStack.remove(lockThread);
        for (Runnable runnable : then) {
            try {
                runnable.run();
            } catch (Throwable t) {
                ctx.runner.profile.logError(t);
            }
        }
        then.clear();
        this.notifyAll();
        if (ctx == null) return;
        synchronized (ctx) {
            if (lockThread != null)
                ctx.events.remove(lockThread);
        }
    }

    @Override
    public String toString() {
        return String.format("ContextContainer:{\"locked\": %s, \"lockThread\": \"%s\"}", locked, lockThread.getName());
    }

}
