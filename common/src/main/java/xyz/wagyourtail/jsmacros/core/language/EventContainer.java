package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * @param <T>
 * @since 1.4.0
 */
public class EventContainer<T> {
    private final BaseScriptContext<T> ctx;
    private Thread lockThread;
    private boolean locked = true;

    public EventContainer(BaseScriptContext<T> ctx) {
        this.ctx = ctx;
    }

    public synchronized boolean isLocked() {
        return locked;
    }

    public void setLockThread(Thread lockThread) {
        if (this.lockThread != null) throw new AssertionError("Cannot change lock thread of context container once assigned!");
        this.lockThread = lockThread;
        ctx.events.put(lockThread, this);
    }
    
    public BaseScriptContext<T> getCtx() {
        return ctx;
    }
    
    public Thread getLockThread() {
        return lockThread;
    }
    
    /**
    * careful with this one it can cause deadlocks if used in scripts incorrectly.
     * @param then must be a {@link MethodWrapper} when called from a script.
     * @throws InterruptedException
     * @since 1.4.0
     */
    public synchronized void awaitLock(Runnable then) throws InterruptedException {
        if (ctx.threads.contains(Thread.currentThread())) {
            if (!(then instanceof MethodWrapper)) {
                throw new AssertionError("For your safety, please use MethodWrapper in scripts.");
            }
        }
        if (locked) {
            this.wait();
        }
        try {
            if (then != null)
                then.run();
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }
    
    /**
    * can be released earlier in a script or language impl.
    * @since 1.4.0
     */
    public synchronized void releaseLock() {
        locked = false;
        Core.getInstance().profile.joinedThreadStack.remove(lockThread);
        this.notifyAll();
        synchronized (ctx) {
            ctx.events.remove(lockThread);
        }
    }

    @Override
    public String toString() {
        return String.format("ContextContainer:{\"locked\": %s, \"lockThread\": \"%s\"}", locked, lockThread.getName());
    }

}
