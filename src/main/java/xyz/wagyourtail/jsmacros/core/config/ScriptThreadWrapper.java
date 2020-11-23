package xyz.wagyourtail.jsmacros.core.config;

public class ScriptThreadWrapper {
    public final Thread t;
    public final ScriptTrigger m;
    public final long startTime;
    
    public ScriptThreadWrapper(Thread t, ScriptTrigger m, long startTime) {
        this.t = t;
        this.m = m;
        this.startTime = startTime;
    }
    
    public void start() {
        t.start();
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public Thread getThread() {
        return t;
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public ScriptTrigger getRawScript() {
        return m;
    }
    
    /**
     * @since 1.2.7
     * @return
     */
    public long getStartTime() {
        return startTime;
    }
    
}
