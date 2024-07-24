package xyz.wagyourtail.jsmacros.core.language;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.service.ServiceManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T>
 * @since 1.4.0
 */
public abstract class BaseScriptContext<T> {
    public final Core<?, ?> runner;
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

    protected final Map<Thread, EventContainer<? extends BaseScriptContext<T>>> events = new ConcurrentHashMap<>();

    // <listener, event>
    public final WeakHashMap<IEventListener, String> eventListeners = new WeakHashMap<>();

    public boolean hasMethodWrapperBeenInvoked = false;

    public BaseScriptContext(Core<?, ?> runner, BaseEvent event, File file) {
        this.runner = runner;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldKeepAlive() {
        return this.hasMethodWrapperBeenInvoked || ServiceManager.hasKeepAlive(this);
    }

    /**
     * @return
     * @since 1.6.0
     */
    public synchronized Map<Thread, EventContainer<? extends BaseScriptContext<T>>> getBoundEvents() {
        return events;
    }

    /**
     * @param th
     * @param event
     * @since 1.6.0
     */
    public synchronized void bindEvent(Thread th, EventContainer<BaseScriptContext<T>> event) {
        events.put(th, event);
    }

    /**
     * @param thread
     * @return
     * @since 1.6.0
     */
    public synchronized boolean releaseBoundEventIfPresent(Thread thread) {
        EventContainer<? extends BaseScriptContext<T>> event = events.get(thread);
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
     * @return
     * @since 1.5.0
     */
    public Thread getMainThread() {
        return mainThread;
    }

    /**
     * @param t
     * @return is a newly bound thread
     * @since 1.6.0
     */
    public synchronized boolean bindThread(Thread t) {
        if (closed) {
            throw new ScriptAssertionError("Cannot bind thread to closed context");
        }
        if (t == null) {
            throw new ScriptAssertionError("Cannot bind null thread");
        }
        return threads.add(t);
    }

    /**
     * @param t
     * @since 1.6.0
     */
    public synchronized void unbindThread(Thread t) {
        if (!threads.remove(t)) throw new ScriptAssertionError("Cannot unbind thread that is not bound");
        EventContainer<?> container = events.get(t);
        if (container != null) {
            container.releaseLock();
        }
    }

    /**
     * @return
     * @since 1.6.0
     */
    public synchronized Set<Thread> getBoundThreads() {
        return threads;
    }

    /**
     * @param t
     * @since 1.5.0
     */
    public void setMainThread(Thread t) {
        if (this.mainThread != null) {
            throw new ScriptAssertionError("Cannot change main thread of context container once assigned!");
        }
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
        if (this.context != null) {
            throw new ScriptAssertionError("Context already set");
        }
        this.context = context;
    }

    public synchronized boolean isContextClosed() {
        if (syncObject.get() == null) {
            if (!closed) {
                closeContext();
            }
        }
        return closed;
    }

    public synchronized void closeContext() {
        closed = true;
        // fix concurrency issue the "fun" way
        ImmutableList.copyOf(getBoundEvents().values()).forEach(EventContainer::releaseLock);
        ImmutableSet.copyOf(getBoundThreads()).forEach(Thread::interrupt);
        runner.getContexts().remove(this);
    }

    /**
     * @return
     * @since 1.6.0
     */
    @Nullable
    public File getFile() {
        return mainFile;
    }

    /**
     * @return
     * @since 1.6.0
     */
    public File getContainedFolder() {
        return mainFile == null ? runner.config.macroFolder.getAbsoluteFile() : mainFile.getParentFile().getAbsoluteFile();
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
