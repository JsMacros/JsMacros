package xyz.wagyourtail.jsmacros.core.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.4.0
 * @param <T>
 */
public abstract class BaseScriptContext<T> {
    protected boolean closed = false;
    public final long startTime = System.currentTimeMillis();
    public final BaseEvent triggeringEvent;
    protected final File mainFile;

    /**
     * the actual "context", for whatever the language impl is...
     */
    protected T context = null;
    protected Thread mainThread = null;

    protected final Set<Thread> threads = new HashSet<>();

    protected final Map<Thread, EventContainer<T>> events = new HashMap<>();

    public boolean hasMethodWrapperBeenInvoked = false;

    public BaseScriptContext(BaseEvent event, File file) {
        this.triggeringEvent = event;
        this.mainFile = file;
    }

    /**
     * @since 1.6.0
     * @return
     */
    public Map<Thread, EventContainer<T>> getBoundEvents() {
        return ImmutableMap.copyOf(events);
    }

    /**
     * @since 1.6.0
     * @param th
     * @param event
     */
    public synchronized void bindEvent(Thread th, EventContainer<T> event) {
        events.put(th, event);
    }

    /**
     * @since 1.6.0
     * @param thread
     *
     * @return
     */
    public synchronized boolean releaseBoundEventIfPresent(Thread thread) {
        EventContainer<T> event = events.get(thread);
        if (event != null) {
            event.releaseLock();
            return true;
        }
        return false;
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
    public synchronized boolean bindThread(Thread t) {
        if (closed) throw new AssertionError("Cannot bind thread to closed context");
        return threads.add(t);
    }

    /**
     * @since 1.6.0
     * @param t
     */
    public synchronized void unbindThread(Thread t) {
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
        return ImmutableSet.copyOf(threads);
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
    
    public boolean isContextClosed() {
        return closed;
    }
    
    public synchronized void closeContext() {
        closed = true;
        events.values().forEach(EventContainer::releaseLock);
        threads.forEach(Thread::interrupt);
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
