package xyz.wagyourtail.jsmacros.core.language;

import java.util.concurrent.Semaphore;

public class ContextContainer<T> {
    private final ScriptContext<T> ctx;
    private Thread lockThread;
    private final Semaphore lock;
    
    public ContextContainer(ScriptContext<T> ctx, Semaphore lock) {
        this.ctx = ctx;
        this.lock = lock;
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
     * can be released early in a script or language impl.
     * @return semaphore used for synchronous stuff,
     */
    public Semaphore getLock() {
        return lock;
    }
    
}
