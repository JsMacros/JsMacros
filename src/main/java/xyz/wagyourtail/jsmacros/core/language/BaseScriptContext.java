package xyz.wagyourtail.jsmacros.core.language;

import io.netty.util.internal.ConcurrentSet;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 1.4.0
 * @param <T>
 */
public abstract class BaseScriptContext<T> {
    public final long startTime = System.currentTimeMillis();
    public final BaseEvent triggeringEvent;
    protected final File mainFile;

    /**
     * the actual "context", for whatever the language impl is...
     */
    protected T context = null;
    protected Thread mainThread = null;

    protected final Set<Thread> threads = new ConcurrentSet<>();

    public final Map<Thread, EventContainer<?>> events = new ConcurrentHashMap<>();

    public boolean hasMethodWrapperBeenInvoked = false;

    public BaseScriptContext(BaseEvent event, File file) {
        this.triggeringEvent = event;
        this.mainFile = file;
    }

    public T getContext() {
        return context;
    }

    /**
     * @since 1.5.0
     * @return
     */
    public Thread getMainThread() {
        return mainThread;
    }

    /**
     * @since 1.6.0
     * @param t
     * @return is a newly bound thread
     */
    public boolean bindThread(Thread t) {
        return threads.add(t);
    }

    /**
     * @since 1.6.0
     * @param t
     */
    public void unbindThread(Thread t) {
        if (!threads.remove(t)) {
            throw new AssertionError("Thread was not bound");
        }
        EventContainer<?> container = events.get(t);
        if (container != null) {
            container.releaseLock();
        }
    }

    /**
     * @since 1.6.0
     * @return
     */
    public Set<Thread> getBoundThreads() {
        return threads;
    }

    /**
     * @since 1.5.0
     * @param t
     */
    public void setMainThread(Thread t) {
        if (this.mainThread != null) throw new AssertionError("Cannot change main thread of context container once assigned!");
        this.mainThread = t;
        bindThread(t);
    }

    /**
     * @since 1.5.0
     */
    public BaseEvent getTriggeringEvent() {
        return triggeringEvent;
    }

    public void setContext(T context) {
        if (this.context != null) throw new RuntimeException("Context already set");
        this.context = context;
    }
    
    public abstract boolean isContextClosed();
    
    public void closeContext() {
        events.values().forEach(EventContainer::releaseLock);
        Core.instance.getContexts().remove(this);
    }

    /**
     * @since 1.6.0
     * @return
     */
     @Nullable
    public File getFile() {
        return mainFile;
    }

    /**
     * @since 1.6.0
     * @return
     */
    public File getContainedFolder() {
        return mainFile == null ? Core.instance.config.macroFolder : mainFile.getParentFile();
    }
}
