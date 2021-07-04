package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * @param <T>
 * @since 1.4.0
 */
public class ContextContainer<T> {
    private final ScriptContext<T> ctx;
    private final Thread rootThread;
    private Thread lockThread;
    private boolean locked = true;

    public ContextContainer(ScriptContext<T> ctx) {
        this(ctx, Thread.currentThread());
    }

    public ContextContainer(ScriptContext<T> ctx, Thread rootThread) {
        this.ctx = ctx;
        this.rootThread = rootThread;
    }

    public synchronized boolean isLocked() {
        return locked;
    }

    public void setLockThread(Thread lockThread) {
        if (this.lockThread != null) throw new AssertionError("Cannot change lock thread of context container once assigned!");
        this.lockThread = lockThread;
    }
    
    public ScriptContext<T> getCtx() {
        return ctx;
    }
    
    public Thread getLockThread() {
        return lockThread;
    }

    /**
     * @since 1.5.0
     * @return
     */
    public Thread getRootThread() {
        return rootThread;
    }
    
    /**
    * careful with this one it can cause deadlocks if used in scripts incorrectly.
     * @param then must be a {@link MethodWrapper} when called from a script.
     * @throws InterruptedException
     * @since 1.4.0
     */
    public synchronized void awaitLock(Runnable then) throws InterruptedException {
        if (Core.instance.threadContext.containsKey(Thread.currentThread())) {
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
    }

    @Override
    public String toString() {
        return String.format("ContextContainer:{\"locked\": %s, \"lockThread\": \"%s\"}", locked, lockThread.getName());
    }

}
