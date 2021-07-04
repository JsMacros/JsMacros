package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

import java.lang.ref.WeakReference;

/**
 * @since 1.4.0
 * @param <T>
 */
public abstract class ScriptContext<T> {
    public final long startTime = System.currentTimeMillis();
    protected WeakReference<T> context = null;
    protected WeakReference<Thread> mainThread = null;

    protected final BaseEvent triggeringEvent;

    public ScriptContext(BaseEvent event) {
        this.triggeringEvent = event;
    }

    public WeakReference<T> getContext() {
        return context;
    }

    /**
     * @since 1.5.0
     * @return
     */
    public WeakReference<Thread> getMainThread() {
        return mainThread;
    }

    /**
     * @since 1.5.0
     * @param t
     */
    public void setMainThread(Thread t) {
        if (this.mainThread != null) throw new AssertionError("Cannot change main thread of context container once assigned!");
        this.mainThread = new WeakReference<>(t);
    }

    /**
     * @since 1.5.0
     */
    public BaseEvent getTriggeringEvent() {
        return triggeringEvent;
    }

    public void setContext(T context) {
        if (this.context != null) throw new RuntimeException("Context already set");
        this.context = new WeakReference<>(context);
    }
    
    public boolean isContextClosed() {
        return context == null || context.get() == null;
    }
    
    public abstract void closeContext();
}
