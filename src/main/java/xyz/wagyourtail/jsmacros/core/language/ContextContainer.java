package xyz.wagyourtail.jsmacros.core.language;

import java.util.concurrent.Semaphore;

public class ContextContainer<T> {
    private final ScriptContext<T> ctx;
    private Thread lockThread;
    private final Semaphore lock = new Semaphore(0);
    
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
     * DO NOT USE IN A SCRIPT PLEASE, MAKE YOUR OWN MUTEX/SEMAPHORES
     * @throws InterruptedException
     */
    public void awaitLock() throws InterruptedException {
        lock.acquire();
    }
    
    /**
     * can be released earlier in a script or language impl.
     * @return semaphore used for synchronous stuff,
     */
    public void releaseLock() {
        lock.release();
    }
    
}
