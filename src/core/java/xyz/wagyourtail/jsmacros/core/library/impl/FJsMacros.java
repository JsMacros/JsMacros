package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.*;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.event.impl.FiltererComposed;
import xyz.wagyourtail.jsmacros.core.event.impl.FiltererInverted;
import xyz.wagyourtail.jsmacros.core.event.impl.FiltererModulus;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.WrappedScript;
import xyz.wagyourtail.jsmacros.core.service.ServiceManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Functions that interact directly with JsMacros or Events.
 * <p>
 * An instance of this class is passed to scripts as the {@code JsMacros} variable.
 *
 * @author Wagyourtail
 */
@Library("JsMacros")
@SuppressWarnings("unused")
public class FJsMacros extends PerExecLibrary {

    public FJsMacros(BaseScriptContext<?> context) {
        super(context);
    }

    /**
     * @return the JsMacros profile class.
     */
    public BaseProfile getProfile() {
        return runner.profile;
    }

    /**
     * @return the JsMacros config management class.
     */
    public ConfigManager getConfig() {
        return runner.config;
    }

    /**
     * services are background scripts designed to run full time and are mainly noticed by their side effects.
     *
     * @return for managing services.
     * @since 1.6.3
     */
    public ServiceManager getServiceManager() {
        return runner.services;
    }

    /**
     * @return list of non-garbage-collected ScriptContext's
     * @since 1.4.0
     */
    public List<BaseScriptContext<?>> getOpenContexts() {
        return ImmutableList.copyOf(runner.getContexts());
    }

    /**
     * @param file
     * @see FJsMacros#runScript(String, String, MethodWrapper)
     * @since 1.1.5
     */
    public EventContainer<?> runScript(String file) {
        return runScript(file, (EventCustom) null, null);
    }

    /**
     * @param file
     * @param fakeEvent you probably actually want to pass an instance created by {@link #createCustomEvent(String)}
     * @return
     * @since 1.6.3
     */
    public EventContainer<?> runScript(String file, @Nullable BaseEvent fakeEvent) {
        return runScript(file, fakeEvent, null);
    }

    /**
     * runs a script with a eventCustom to be able to pass args
     *
     * @param file
     * @param fakeEvent
     * @param callback
     * @return container the script is running on.
     * @since 1.6.3 (1.1.5 - 1.6.3 didn't have fakeEvent)
     */
    public EventContainer<?> runScript(String file, @Nullable BaseEvent fakeEvent, @Nullable MethodWrapper<Throwable, Object, Object, ?> callback) {
        if (callback != null) {
            return runner.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", Path.of(file), true, false), fakeEvent, () -> callback.accept(null), callback);
        } else {
            return runner.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", Path.of(file), true, false), fakeEvent, null, null);
        }
    }

    /**
     * @param language
     * @param script
     * @return
     * @see FJsMacros#runScript(String, String, MethodWrapper)
     * @since 1.2.4
     */
    public EventContainer<?> runScript(String language, String script) {
        return runScript(language, script, null);
    }

    /**
     * Runs a string as a script.
     *
     * @param language
     * @param script
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link String}&gt;
     * @return the {@link EventContainer} the script is running on.
     * @since 1.2.4
     */
    public EventContainer<?> runScript(String language, String script, @Nullable MethodWrapper<Throwable, Object, Object, ?> callback) {
        return runScript(language, script, null, callback);
    }

    /**
     * @param language
     * @param script
     * @param file
     * @param callback
     * @return
     * @since 1.6.0
     */
    public EventContainer<?> runScript(String language, String script, @Nullable String file, @Nullable MethodWrapper<Throwable, Object, Object, ?> callback) {
        return runScript(language, script, file, null, callback);
    }

    /**
     * @param language
     * @param script
     * @param file
     * @param event
     * @param callback
     * @return
     * @since 1.7.0
     */
    public EventContainer<?> runScript(String language, String script, @Nullable String file, @Nullable BaseEvent event, @Nullable MethodWrapper<Throwable, Object, Object, ?> callback) {
        if (callback != null) {
            return runner.exec(language, script, file != null ? ctx.getContainedFolder().toPath().resolve(file).toFile() : null, event, () -> callback.accept(null), callback);
        } else {
            return runner.exec(language, script, file != null ? ctx.getContainedFolder().toPath().resolve(file).toFile() : null, event, null, null);
        }
    }

    /**
     * @param file
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     * @since 1.7.0
     */
    public <T, U, R> MethodWrapper<T, U, R, ?> wrapScriptRun(String file) {
        return new WrappedScript<>(runner, (e) -> (EventContainer<BaseScriptContext<?>>) runner.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, e.getEventName(), Path.of(file), true, false), e, null, null), false);
    }

    /**
     * @param language
     * @param script
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     * @since 1.7.0
     */
    public <T, U, R> MethodWrapper<T, U, R, ?> wrapScriptRun(String language, String script) {
        return new WrappedScript<>(runner, (e) -> (EventContainer<BaseScriptContext<?>>) runner.exec(language, script, null, e, null, null), false);
    }

    /**
     * @param language
     * @param script
     * @param file
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     * @since 1.7.0
     */
    public <T, U, R> MethodWrapper<T, U, R, ?> wrapScriptRun(String language, String script, @Nullable String file) {
        return new WrappedScript<>(runner, (e) -> (EventContainer<BaseScriptContext<?>>) runner.exec(language, script, file != null ? ctx.getContainedFolder().toPath().resolve(file).toFile() : null, e, null, null), false);
    }

    /**
     * @param file
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     * @since 1.7.0
     */
    public <T, U, R> MethodWrapper<T, U, R, ?> wrapScriptRunAsync(String file) {
        return new WrappedScript<>(runner, (e) -> (EventContainer<BaseScriptContext<?>>) runner.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, e.getEventName(), Path.of(file), true, false), e, null, null), true);
    }

    /**
     * @param language
     * @param script
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     * @since 1.7.0
     */
    public <T, U, R> MethodWrapper<T, U, R, ?> wrapScriptRunAsync(String language, String script) {
        return new WrappedScript<>(runner, (e) -> (EventContainer<BaseScriptContext<?>>) runner.exec(language, script, null, e, null, null), true);
    }

    /**
     * @param language
     * @param script
     * @param file
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     * @since 1.7.0
     */
    public <T, U, R> MethodWrapper<T, U, R, ?> wrapScriptRunAsync(String language, String script, @Nullable String file) {
        return new WrappedScript<>(runner, (e) -> (EventContainer<BaseScriptContext<?>>) runner.exec(language, script, file != null ? ctx.getContainedFolder().toPath().resolve(file).toFile() : null, e, null, null), true);
    }

    /**
     * Opens a file with the default system program.
     *
     * @param path relative to the script's folder.
     * @since 1.1.8
     * @deprecated use the Utils library instead.
     */
    @Deprecated
    public void open(String path) throws IOException {
        openUrl(ctx.getContainedFolder().toPath().resolve(path).toUri().toURL());
    }

    /**
     * @param url
     * @throws MalformedURLException
     * @since 1.6.0
     * @deprecated use the Utils library instead.
     */
    @Deprecated
    public void openUrl(String url) throws IOException {
        openUrl(new URL(url));
    }

    protected void openUrl(URL url) throws IOException {
        String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String urlOpen[];
        if (string.contains("mac") || string.contains("darwin")) {
            urlOpen = new String[]{"open", url.toString()};
        } else if (string.contains("win")) {
            urlOpen = new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
        } else {
            String s2 = url.toString();
            if ("file".equals(url.getProtocol())) {
                s2 = s2.replace("file:", "file://");
            }
            urlOpen = new String[]{"xdg-open", s2};
        }

        Process process = (Process) Runtime.getRuntime().exec(urlOpen);

        for (String s2 : IOUtils.readLines(process.getErrorStream(), Charset.defaultCharset())) {
            runner.config.LOGGER.error(s2);
        }

        process.getInputStream().close();
        process.getErrorStream().close();
        process.getOutputStream().close();
    }

    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     *
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return
     * @see IEventListener
     * @since 1.2.7
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, callback: MethodWrapper<Events[E], EventContainer>")
    public IEventListener on(String event, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        return on(event, null, false, callback);
    }

    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     *
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return
     * @see IEventListener
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, joined: boolean, callback: MethodWrapper<Events[E], EventContainer>")
    public IEventListener on(String event, boolean joined, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        return on(event, null, joined, callback);
    }

    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     *
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return
     * @see IEventListener
     * @since 1.9.1
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, filterer: EventFilterer, callback: MethodWrapper<Events[E], EventContainer>")
    public IEventListener on(String event, EventFilterer filterer, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        return on(event, filterer, false, callback);
    }

    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     *
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return
     * @see IEventListener
     * @since 1.9.1
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, filterer: EventFilterer, joined: boolean, callback: MethodWrapper<Events[E], EventContainer>")
    public IEventListener on(String event, EventFilterer filterer, boolean joined, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        if (callback == null) {
            return null;
        }
        if (!runner.eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }
        if (filterer != null && !filterer.canFilter(event)) {
            throw new IllegalArgumentException(String.format("Provided filterer (%s) cannot be used to filter %s event!", filterer.getClass().getSimpleName(), event));
        }
        Thread th = Thread.currentThread();
        String creatorName = th.getName();
        IEventListener listener = new ScriptEventListener() {

            @Override
            public boolean joined() {
                return joined;
            }

            @Override
            public EventContainer<?> trigger(BaseEvent e) {
                if (filterer != null && !filterer.test(e)) return null;
                EventContainer<?> p = new EventContainer<>(callback.getCtx());
                Thread ot = callback.overrideThread();
                Thread th = runner.threadPool.runTask(() -> {
                    Thread t = Thread.currentThread();
                    t.setName(this.toString());
                    try {
                        callback.accept(e, p);
                    } catch (Throwable ex) {
                        runner.eventRegistry.removeListener(event, this);
                        runner.profile.logError(ex);
                    } finally {
                        p.releaseLock();
                    }
                });
                p.setLockThread(ot == null ? th : ot);
                return p;
            }

            @Override
            public String getCreatorName() {
                return creatorName;
            }

            @Override
            public MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper() {
                return callback;
            }

            @Override
            public BaseScriptContext<?> getCtx() {
                return callback.getCtx();
            }

            @Override
            public void off() {
                runner.eventRegistry.removeListener(event, this);
            }

            @Override
            public String toString() {
                return String.format("ScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", getCreatorName(), event);
            }
        };
        runner.eventRegistry.addListener(event, listener);
        ctx.eventListeners.put(listener, event);
        return listener;
    }

    /**
     * Creates a single-run listener for an event, this function can be more efficient that running a script file when used properly.
     *
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return the listener.
     * @see IEventListener
     * @since 1.2.7
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, callback: MethodWrapper<Events[E], EventContainer>")
    public IEventListener once(String event, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        return once(event, false, callback);
    }


    /**
     * Creates a single-run listener for an event, this function can be more efficient that running a script file when used properly.
     *
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return the listener.
     * @see IEventListener
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, joined: boolean, callback: MethodWrapper<Events[E], EventContainer>")
    public IEventListener once(String event, boolean joined, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        if (callback == null) {
            return null;
        }
        if (!runner.eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }
        Thread th = Thread.currentThread();
        String creatorName = th.getName();
        IEventListener listener = new ScriptEventListener() {
            @Override
            public boolean joined() {
                return joined;
            }

            @Override
            public EventContainer<?> trigger(BaseEvent e) {
                runner.eventRegistry.removeListener(event, this);
                EventContainer<?> p = new EventContainer<>(callback.getCtx());
                Thread ot = callback.overrideThread();
                Thread th = runner.threadPool.runTask(() -> {
                    Thread t = Thread.currentThread();

                    t.setName(this.toString());
                    try {
                        callback.accept(e, p);
                    } catch (Throwable ex) {
                        runner.profile.logError(ex);
                    } finally {
                        p.releaseLock();
                    }
                });
                p.setLockThread(ot == null ? th : ot);
                return p;
            }

            @Override
            public String getCreatorName() {
                return creatorName;
            }

            @Override
            public MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper() {
                return callback;
            }

            @Override
            public BaseScriptContext<?> getCtx() {
                return callback.getCtx();
            }

            @Override
            public void off() {
                runner.eventRegistry.removeListener(event, this);
            }

            @Override
            public String toString() {
                return String.format("OnceScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", getCreatorName(), event);
            }

        };
        runner.eventRegistry.addListener(event, listener);
        ctx.eventListeners.put(listener, event);
        return listener;
    }

    /**
     * @param listener
     * @return
     * @see FJsMacros#off(String, IEventListener)
     * @since 1.2.3
     */
    public boolean off(IEventListener listener) {
        return runner.eventRegistry.removeListener(listener);
    }

    /**
     * Removes a {@link IEventListener IEventListener} from an event.
     *
     * @param event
     * @param listener
     * @return
     * @see IEventListener
     * @since 1.2.3
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, listener: IEventListener")
    public boolean off(String event, IEventListener listener) {
        return runner.eventRegistry.removeListener(event, listener);
    }

    /**
     * Will also disable all listeners for the given event, including JsMacros own event listeners.
     *
     * @param event the event to remove all listeners from
     * @since 1.8.4
     */
    @DocletReplaceParams("event: keyof Events")
    public void disableAllListeners(String event) {
        for (IEventListener listener : ImmutableList.copyOf(runner.eventRegistry.getListeners(event))) {
            listener.off();
        }
    }

    /**
     * Will also disable all listeners, including JsMacros own event listeners.
     *
     * @since 1.8.4
     */
    public void disableAllListeners() {
        for (Map.Entry<String, Set<IEventListener>> entry : runner.eventRegistry.getListeners().entrySet()) {
            for (IEventListener listener : ImmutableList.copyOf(entry.getValue())) {
                listener.off();
            }
        }
    }

    /**
     * Will only disable user created event listeners for the given event. This includes listeners
     * created from {@link #on(String, MethodWrapper)}, {@link #once(String, MethodWrapper)},
     * {@link #waitForEvent(String)}, {@link #waitForEvent(String, MethodWrapper)} and
     * {@link #waitForEvent(String, MethodWrapper, MethodWrapper)}.
     *
     * @param event the event to remove all listeners from
     * @since 1.8.4
     */
    @DocletReplaceParams("event: keyof Events")
    public void disableScriptListeners(String event) {
        for (IEventListener listener : ImmutableList.copyOf(runner.eventRegistry.getListeners(event))) {
            if (listener instanceof ScriptEventListener) {
                listener.off();
            }
        }
    }

    /**
     * Will only disable user created event listeners.  This includes listeners created from
     * {@link #on(String, MethodWrapper)}, {@link #once(String, MethodWrapper)},
     * {@link #waitForEvent(String)}, {@link #waitForEvent(String, MethodWrapper)} and
     * {@link #waitForEvent(String, MethodWrapper, MethodWrapper)}.
     *
     * @since 1.8.4
     */
    public void disableScriptListeners() {
        for (Map.Entry<String, Set<IEventListener>> entry : runner.eventRegistry.getListeners().entrySet()) {
            for (IEventListener listener : ImmutableList.copyOf(entry.getValue())) {
                if (listener instanceof ScriptEventListener) {
                    listener.off();
                }
            }
        }
    }

    /**
     * @param event event to wait for
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     * @throws InterruptedException
     * @since 1.5.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E")
    @DocletReplaceReturn("FJsMacros$EventAndContext<Events[E]>")
    public EventAndContext<?> waitForEvent(String event) throws InterruptedException {
        return waitForEvent(event, null, null);
    }

    /**
     * @param event event to wait for
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     * @throws InterruptedException
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, join: boolean")
    @DocletReplaceReturn("FJsMacros$EventAndContext<Events[E]>")
    public EventAndContext<?> waitForEvent(String event, boolean join) throws InterruptedException {
        return waitForEvent(event, join, null, null);
    }

    /**
     * @param event
     * @return
     * @throws InterruptedException
     * @since 1.5.0 [citation needed]
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, filter: MethodWrapper<Events[E], undefined, boolean> | null")
    @DocletReplaceReturn("FJsMacros$EventAndContext<Events[E]>")
    public EventAndContext<?> waitForEvent(String event, @Nullable MethodWrapper<BaseEvent, Object, Boolean, ?> filter) throws InterruptedException {
        return waitForEvent(event, filter, null);
    }


    /**
     * @param event
     * @return
     * @throws InterruptedException
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, join: boolean, filter: MethodWrapper<Events[E], undefined, boolean> | null")
    @DocletReplaceReturn("FJsMacros$EventAndContext<Events[E]>")
    public EventAndContext<?> waitForEvent(String event, boolean join, @Nullable MethodWrapper<BaseEvent, Object, Boolean, ?> filter) throws InterruptedException {
        return waitForEvent(event, join, filter, null);
    }

    /**
     * waits for an event. if this thread is bound to an event already, this will release current lock.
     *
     * @param event            event to wait for
     * @param filter           filter the event until it has the proper values or whatever.
     * @param runBeforeWaiting runs as a {@link Runnable}, run before waiting, this is a thread-safety thing to prevent "interrupts" from going in between this and things like deferCurrentTask
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     * @throws InterruptedException
     * @since 1.5.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, filter: MethodWrapper<Events[E], undefined, boolean> | null, runBeforeWaiting: MethodWrapper<JavaObject, JavaObject, JavaObject> | null")
    @DocletReplaceReturn("FJsMacros$EventAndContext<Events[E]>")
    public EventAndContext<?> waitForEvent(String event, @Nullable MethodWrapper<BaseEvent, Object, Boolean, ?> filter, @Nullable MethodWrapper<Object, Object, Object, ?> runBeforeWaiting) throws InterruptedException {
        return waitForEvent(event, false, filter, runBeforeWaiting);
    }



    /**
     * waits for an event. if this thread is bound to an event already, this will release current lock.
     *
     * @param event            event to wait for
     * @param filter           filter the event until it has the proper values or whatever.
     * @param runBeforeWaiting runs as a {@link Runnable}, run before waiting, this is a thread-safety thing to prevent "interrupts" from going in between this and things like deferCurrentTask
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     * @throws InterruptedException
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: E, join: boolean, filter: MethodWrapper<Events[E], undefined, boolean> | null, runBeforeWaiting: MethodWrapper<JavaObject, JavaObject, JavaObject> | null")
    @DocletReplaceReturn("FJsMacros$EventAndContext<Events[E]>")
    public EventAndContext<?> waitForEvent(String event, boolean join, @Nullable MethodWrapper<BaseEvent, Object, Boolean, ?> filter, @Nullable MethodWrapper<Object, Object, Object, ?> runBeforeWaiting) throws InterruptedException {
        // event return values
        final BaseEvent[] ev = {null};
        // create a new event container so we can actually release joined events
        EventContainer<?>[] ctxCont = new EventContainer[]{new EventContainer<>(ctx)};
        ctx.wrapSleep(() -> {
            if (!runner.eventRegistry.events.contains(event)) {
                throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
            }

            //get current thread establish the lock to use for waiting blah blah blah
            Thread th = Thread.currentThread();
            String creatorName = th.getName();
            Semaphore lock = new Semaphore(0);
            Semaphore lock2 = new Semaphore(0);

            boolean[] done = new boolean[]{false};

            // create the listener
            IEventListener listener = new ScriptEventListener() {
                @Override
                public boolean joined() {
                    return join;
                }

                @Override
                public EventContainer<?> trigger(BaseEvent evt) {
                    ev[0] = evt;
                    // allow for initial thread to run its filter
                    lock.release();
                    try {
                        // wait for filter to finish
                        lock2.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                    // if filter done, we can remove self and return the event context
                    if (done[0]) {
                        runner.eventRegistry.removeListener(event, this);
                        ctx.bindEvent(th, (EventContainer) ctxCont[0]);
                        return ctxCont[0];
                    }
                    return null;
                }

                @Override
                public String getCreatorName() {
                    return creatorName;
                }

                @Override
                public MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper() {
                    return null;
                }

                @Override
                public BaseScriptContext<?> getCtx() {
                    return ctx;
                }

                @Override
                public void off() {
                    runner.eventRegistry.removeListener(event, this);
                    th.interrupt();
                }

                @Override
                public String toString() {
                    return String.format("WaitForEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
                }
            };
            // register the listener
            runner.eventRegistry.addListener(event, listener);
            ctx.eventListeners.put(listener, event);

            // run before, this is a thread-safety thing to prevent "interrupts" from going in between this and things like deferCurrentTask
            // it is thread safe because we already registered the listener so we won't miss any events
            if (runBeforeWaiting != null) {
                runBeforeWaiting.run();
            }

            // make sure the current context isn't still locked.
            ctx.releaseBoundEventIfPresent(th);

            // set the new EventContainer's lock
            ctxCont[0].setLockThread(th);

            // waits for event
            while (!done[0]) {
                lock.acquire();
                try {
                    // check the filter
                    done[0] = filter == null || filter.test(ev[0]);
                } catch (Throwable ex) {
                    runner.eventRegistry.removeListener(event, listener);
                    throw new RuntimeException("Error thrown in filter", ex);
                } finally {
                    lock2.release();
                }
            }

        });
        // returns new context and event value to the user so they can release joined stuff early
        return new EventAndContext<>(ev[0], ctxCont[0]);
    }

    /**
     * @param event
     * @return a list of script-added listeners.
     * @since 1.2.3
     */
    @DocletReplaceParams("event: keyof Events")
    public List<IEventListener> listeners(String event) {
        List<IEventListener> listeners = new ArrayList<>();
        for (IEventListener l : runner.eventRegistry.getListeners(event)) {
            if (!(l instanceof BaseListener)) {
                listeners.add(l);
            }
        }
        return listeners;
    }

    /**
     * create an event filterer.<br>
     * this exists to reduce lag when listening to frequently triggered events.
     * @since 1.9.1
     */
    @DocletReplaceTypeParams("E extends keyof EventFilterers")
    @DocletReplaceParams("event: E")
    @DocletReplaceReturn("EventFilterers[E]")
    public EventFilterer createEventFilterer(String event) {
        Class<? extends EventFilterer> fclass = runner.eventRegistry.filterableEvents.get(event);
        if (fclass == null) {
            if (runner.eventRegistry.events.contains(event)) {
                throw new IllegalArgumentException(String.format("Event %s doesn't have a filterer class!", event));
            } else {
                throw new IllegalArgumentException(String.format("Event %s not found!", event));
            }
        }
        try {
            return fclass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create a composed event filterer.<br>
     * this filterer combines multiple filterers together with and/or logic.
     * @since 1.9.1
     */
    public FiltererComposed createComposedEventFilterer(EventFilterer initial) {
        return new FiltererComposed(initial);
    }

    /**
     * create a modulus event filterer.<br>
     * this filterer only let every nth event pass through.
     * @since 1.9.1
     */
    public FiltererModulus createModulusEventFilterer(int quotient) {
        return new FiltererModulus(quotient);
    }

    /**
     * inverts the base filterer's result.<br>
     * this checks if the base is already inverted.<br>
     * e.g. {@code filterer == invert(invert(filterer))} would be {@code true}.
     * @since 1.9.1
     */
    public EventFilterer invertEventFilterer(EventFilterer base) {
        return FiltererInverted.invert(base);
    }

    /**
     * create a custom event object that can trigger a event. It's recommended to use
     * {@link EventCustom#registerEvent()} to set up the event to be visible in the GUI.
     *
     * @param eventName name of the event. please don't use an existing one... your scripts might not like that.
     * @return
     * @see BaseEventRegistry#addEvent(String)
     * @since 1.2.8
     */
    public EventCustom createCustomEvent(String eventName) {
        return new EventCustom(runner, eventName);
    }

    /**
     * asserts if {@code event} is the correct type of event<br>
     * and convert {@code event} type to target type in ts<br>
     * example:
     * <pre>
     * JsMacros.assertEvent(event, 'Service')
     * </pre>
     * @param event the event to assert
     * @param type string of the event type
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends keyof Events")
    @DocletReplaceParams("event: Events.BaseEvent, type: E")
    @DocletReplaceReturn("asserts event is Events[E]")
    public void assertEvent(BaseEvent event, String type) {
        if (event == null) throw new AssertionError("event is null!");
        if (type == null) throw new AssertionError("event type is null!");
        if (event.getClass().isAnnotationPresent(Event.class)) {
            String eventName = event.getClass().getAnnotation(Event.class).value();
            if (!eventName.equals(type)) {
                throw new AssertionError(String.format("event type (%s) is not %s!", eventName, type));
            }
        } else {
            throw new AssertionError("The event doesn't have proper event annotation, " + event.getClass().getSimpleName());
        }
    }

    public interface ScriptEventListener extends IEventListener {
        String getCreatorName();

        @Nullable
        MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper();

        BaseScriptContext<?> getCtx();
    }

    public static class EventAndContext<E extends BaseEvent> {
        public final E event;
        public final EventContainer<?> context;

        public EventAndContext(E event, EventContainer<?> context) {
            this.event = event;
            this.context = context;
        }

        public String toString() {
            return String.format("EventAndContext:{\"event\": %s, \"context\": %s}", event.toString(), context.toString());
        }

    }

}
