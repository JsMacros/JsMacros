package xyz.wagyourtail.jsmacros.core.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 1.4.0
 * @param <T>
 */
public abstract class BaseScriptContext<T> {
    protected boolean closed = false;
    public final long startTime = System.currentTimeMillis();

    private Object syncObjectPrivate = new Object();
    public final WeakReference<Object> syncObject = new WeakReference<>(this.syncObjectPrivate);

    public final BaseEvent triggeringEvent;
    protected final File mainFile;

    /**
     * the actual "context", for whatever the language impl is...
     */
    protected T context = null;
    protected Thread mainThread = null;

    protected final Set<Thread> threads = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected final Map<Thread, EventContainer<T>> events = new ConcurrentHashMap<>();

    public boolean hasMethodWrapperBeenInvoked = false;

    public BaseScriptContext(BaseEvent event, File file) {
        this.triggeringEvent = event;
        this.mainFile = file;
    }

    /**
     * this object should only be weak referenced unless we want to prevent the context from closing when syncObject is cleared.
     */
    public Object getSyncObject() {
        return syncObject.get();
    }

    public void clearSyncObject() {
        this.syncObjectPrivate = null;
    }

    /**
     * @since 1.6.0
     * @return
     */
    public synchronized Map<Thread, EventContainer<T>> getBoundEvents() {
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
        if (closed) throw new ScriptAssertionError("Cannot bind thread to closed context");
        if (t == null) throw new ScriptAssertionError("Cannot bind null thread");
        return threads.add(t);
    }

    /**
     * @since 1.6.0
     * @param t
     */
    public synchronized void unbindThread(Thread t) {
        if (!threads.remove(t)) {
            throw new ScriptAssertionError("Thread was not bound");
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
    public synchronized Set<Thread> getBoundThreads() {
        return ImmutableSet.copyOf(threads);
    }

    /**
     * @since 1.5.0
     * @param t
     */
    public void setMainThread(Thread t) {
        if (this.mainThread != null) throw new ScriptAssertionError("Cannot change main thread of context container once assigned!");
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
        if (this.context != null) throw new ScriptAssertionError("Context already set");
        this.context = context;
    }
    
    public synchronized boolean isContextClosed() {
        if (syncObject.get() == null) {
            if (!closed) closeContext();
        }
        return closed;
    }
    
    public synchronized void closeContext() {
        closed = true;
        // fix concurrency issue the "fun" way
        getBoundEvents().values().forEach(EventContainer::releaseLock);
        getBoundThreads().forEach(Thread::interrupt);
        Core.getInstance().getContexts().remove(this);
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
        return mainFile == null ? Core.getInstance().config.macroFolder.getAbsoluteFile() : mainFile.getParentFile().getAbsoluteFile();
    }

    public abstract boolean isMultiThreaded();


    public void wrapSleep(SleepRunnable sleep) throws InterruptedException {
        sleep.run();
    }

    public static class ScriptAssertionError extends AssertionError {
        public ScriptAssertionError(String message) {
            super(message);
        }
    }

    @FunctionalInterface
    public interface SleepRunnable {
        void run() throws InterruptedException;
    }
}
