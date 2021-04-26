package xyz.wagyourtail.jsmacros.core.language;

import java.util.concurrent.Semaphore;

public class ContextContainer<T> {
    private final ScriptContext<T> ctx;
    private Thread lockThread;
    private boolean locked = true;
    
    public ContextContainer(ScriptContext<T> ctx) {
        this.ctx = ctx;
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
     * @throws InterruptedException
     */
    public synchronized void awaitLock() throws InterruptedException {
        if (locked) {
            this.wait();
        }
    }
    
    /**
    * INTERNAL USE ONLY
     */
    public synchronized void releaseLock() {
        locked = false;
        this.notifyAll();
    }
    
}
