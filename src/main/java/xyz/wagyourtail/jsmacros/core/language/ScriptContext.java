package xyz.wagyourtail.jsmacros.core.language;

import java.lang.ref.WeakReference;

public abstract class ScriptContext<T> {
    public final long startTime = System.currentTimeMillis();
    public WeakReference<T> context = null;
    
    public void setContext(T context) {
        if (this.context != null) throw new RuntimeException("Context already set");
        this.context = new WeakReference<>(context);
    }
    
    public boolean isContextClosed() {
        return context == null || context.get() == null;
    }
    
    public abstract void closeContext();
}
