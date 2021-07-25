package xyz.wagyourtail.jsmacros.core.language;

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
        if (then != null) then.run();
    }
    
    /**
    * can be released earlier in a script or language impl.
    * @since 1.4.0
     */
    public synchronized void releaseLock() {
        locked = false;
        this.notifyAll();
        ctx.events.remove(lockThread);
    }

    @Override
    public String toString() {
        return String.format("ContextContainer:{\"locked\": %s, \"lockThread\": \"%s\"}", locked, lockThread.getName());
    }

}
