package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;
import xyz.wagyourtail.jsmacros.core.service.ServiceManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Functions that interact directly with JsMacros or Events.
 * 
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
        return Core.getInstance().profile;
    }

    /**
     * @return the JsMacros config management class.
     */
    public ConfigManager getConfig() {
        return Core.getInstance().config;
    }

    /**
     * services are background scripts designed to run full time and are mainly noticed by their side effects.
     *
     * @since 1.6.3
     * @return for managing services.
     */
    public ServiceManager getServiceManager() {
        return Core.getInstance().services;
    }

    /**
     * @return list of non-garbage-collected ScriptContext's
     * @since 1.4.0
     */
    public List<BaseScriptContext<?>> getOpenContexts() {
        return ImmutableList.copyOf(Core.getInstance().getContexts());
    }

    /**
     * 
     * @see FJsMacros#runScript(String, String, MethodWrapper)  
     * 
     * @since 1.1.5
     * 
     * @param file
     * @return
     */
    public EventContainer<?> runScript(String file) {
        return runScript(file, (EventCustom) null, null);
    }

    /**
     * @since 1.6.3
     * @param file
     * @param fakeEvent you probably actually want to pass an instance created by {@link #createCustomEvent(String)}
     *
     * @return
     */
    public EventContainer<?> runScript(String file, BaseEvent fakeEvent) {
        return runScript(file, fakeEvent, null);
    }

    /**
     * runs a script with a eventCustom to be able to pass args
     * @since 1.6.3 (1.1.5 - 1.6.3 didn't have fakeEvent)
     * @param file
     * @param fakeEvent
     * @param callback
     *
     * @return container the script is running on.
     */
    public EventContainer<?> runScript(String file, BaseEvent fakeEvent, MethodWrapper<Throwable, Object, Object, ?> callback) {
        if (callback != null) {
            return Core.getInstance().exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", Core.getInstance().config.macroFolder.getAbsoluteFile().toPath().resolve(file).toFile(), true), fakeEvent, () -> callback.accept(null), callback);
        } else {
            return Core.getInstance().exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", Core.getInstance().config.macroFolder.getAbsoluteFile().toPath().resolve(file).toFile(), true), fakeEvent, null, null);
        }
    }
    
    /**
     * @see FJsMacros#runScript(String, String, MethodWrapper)
     * 
     * @since 1.2.4
     * 
     * @param language
     * @param script
     * @return
     */
    public EventContainer<?> runScript(String language, String script) {
        return runScript(language, script, null);
    }
    
    /**
     * Runs a string as a script.
     * 
     * @since 1.2.4
     * 
     * @param language
     * @param script
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link String}&gt;
     * @return the {@link EventContainer} the script is running on.
     */
    public EventContainer<?> runScript(String language, String script, MethodWrapper<Throwable, Object, Object, ?> callback) {
        return runScript(language, script, null, callback);
    }

    /**
     * @since 1.6.0
     *
     * @param language
     * @param script
     * @param file
     * @param callback
     *
     * @return
     */
    public EventContainer<?> runScript(String language, String script, String file, MethodWrapper<Throwable, Object, Object, ?> callback) {
        if (callback != null) {
            return Core.getInstance().exec(language, script, file != null ? ctx.getContainedFolder().toPath().resolve(file).toFile() : null, () -> callback.accept(null), callback);
        } else {
            return Core.getInstance().exec(language, script, file != null ? ctx.getContainedFolder().toPath().resolve(file).toFile() : null, null, null);
        }
    }
    
    /**
     * Opens a file with the default system program.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the script's folder.
     */
    public void open(String path) {
        Util.getOperatingSystem().open(ctx.getContainedFolder().toPath().resolve(path).toFile());
    }

    /**
     * @since 1.6.0
     *
     * @param url
     *
     * @throws MalformedURLException
     */
    public void openUrl(String url) throws MalformedURLException {
        Util.getOperatingSystem().open(new URL(url));
    }
    
    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see IEventListener
     * 
     * @since 1.2.7
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return
     */
    public IEventListener on(String event, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        if (callback == null) return null;
        if (!Core.getInstance().eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }
        Thread th = Thread.currentThread();
        IEventListener listener = new ScriptEventListener() {
            
            @Override
            public EventContainer<?> trigger(BaseEvent e) {
                EventContainer<?> p = new EventContainer<>(ctx);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    
                    try {
                        callback.accept(e, p);
                    } catch (Throwable ex) {
                        Core.getInstance().eventRegistry.removeListener(event, this);
                        Core.getInstance().profile.logError(ex);
                    } finally {
                        p.releaseLock();
                    }
                });
                Thread ot = callback.overrideThread();
                p.setLockThread(ot == null ? t : ot);
                t.start();
                return p;
            }
    
            @Override
            public Thread getCreator() {
                return th;
            }
    
            @Override
            public MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper() {
                return callback;
            }
    
            @Override
            public String toString() {
                return String.format("ScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
        };
        Core.getInstance().eventRegistry.addListener(event, listener);
        return listener;
    }
        
    /**
     * Creates a single-run listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see IEventListener
     * 
     * @since 1.2.7
     * 
     * @param event
     * @param callback calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link BaseEvent}, {@link EventContainer}&gt;
     * @return the listener.
     */
    public IEventListener once(String event, MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> callback) {
        if (callback == null) return null;
        if (!Core.getInstance().eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }
        Thread th = Thread.currentThread();
        IEventListener listener = new ScriptEventListener() {
            @Override
            public EventContainer<?> trigger(BaseEvent e) {
                Core.getInstance().eventRegistry.removeListener(event, this);
                EventContainer<?> p = new EventContainer<>(ctx);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(e, p);
                    } catch (Throwable ex) {
                        Core.getInstance().profile.logError(ex);
                    } finally {
                        p.releaseLock();
                    }
                });
                Thread ot = callback.overrideThread();
                p.setLockThread(ot == null ? t : ot);
                t.start();
                return p;
            }
    
            @Override
            public Thread getCreator() {
                return th;
            }
    
            @Override
            public MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper() {
                return callback;
            }
    
            @Override
            public String toString() {
                return String.format("OnceScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
            
        };
        Core.getInstance().eventRegistry.addListener(event, listener);
        return listener;
    }
    
    /**
     * @see FJsMacros#off(String, IEventListener)
     * 
     * @since 1.2.3
     * 
     * @param listener
     * @return
     */
    public boolean off(IEventListener listener) {
        return Core.getInstance().eventRegistry.removeListener(listener);
    }
    
    /**
     * Removes a {@link IEventListener IEventListener} from an event.
     * 
     * @see IEventListener
     * 
     * @since 1.2.3
     * 
     * @param event
     * @param listener
     * @return
     */
    public boolean off(String event, IEventListener listener) {
        return Core.getInstance().eventRegistry.removeListener(event, listener);
    }

    /**
     * @param event event to wait for
     * @since 1.5.0
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     *
     * @throws InterruptedException
     */
    public EventAndContext waitForEvent(String event) throws InterruptedException {
        return waitForEvent(event, null, null);
    }

    /**
     *
     * @param event
     * @return
     * @throws InterruptedException
     */
    public EventAndContext waitForEvent(String event,  MethodWrapper<BaseEvent, Object, Boolean, ?> filter) throws InterruptedException {
        return waitForEvent(event, filter, null);
    }

    /**
     * waits for an event. if this thread is bound to an event already, this will release current lock.
     *
     * @param event event to wait for
     * @param filter filter the event until it has the proper values or whatever.
     * @param runBeforeWaiting runs as a {@link Runnable}, run before waiting, this is a thread-safety thing to prevent "interrupts" from going in between this and things like deferCurrentTask
     * @since 1.5.0
     * @return a event and a new context if the event you're waiting for was joined, to leave it early.
     *
     * @throws InterruptedException
     */
    public EventAndContext waitForEvent(String event, MethodWrapper<BaseEvent, Object, Boolean, ?> filter, MethodWrapper<Object, Object, Object, ?> runBeforeWaiting) throws InterruptedException {
        if (!Core.getInstance().eventRegistry.events.contains(event)) {
            throw new IllegalArgumentException(String.format("Event \"%s\" not found, if it's a custom event register it with 'event.registerEvent()' first.", event));
        }

        //get curent thread establish the lock to use for waiting blah blah blah
        Thread th = Thread.currentThread();
        Semaphore lock = new Semaphore(0);
        Semaphore lock2 = new Semaphore(0);

        // event return values
        final BaseEvent[] ev = {null};
        boolean[] done = new boolean[] {false};

        // create a new event container so we can actually release joined events
        EventContainer<?>[] ctxCont = new EventContainer[] {new EventContainer<>(ctx)};

        // create the listener
        IEventListener listener = new ScriptEventListener() {
            @Override
            public EventContainer<?> trigger(BaseEvent evt) {
                ev[0] = evt;
                // allow for initial thread to run it's filter
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
                    Core.getInstance().eventRegistry.removeListener(event, this);
                    ctx.bindEvent(th, (EventContainer) ctxCont[0]);
                    return ctxCont[0];
                }
                return null;
            }

            @Override
            public Thread getCreator() {
                return th;
            }

            @Override
            public MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper() {
                return null;
            }

            @Override
            public String toString() {
                return String.format("WaitForEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
        };
        // register the listener
        Core.getInstance().eventRegistry.addListener(event, listener);

        // run before, this is a thread-safety thing to prevent "interrupts" from going in between this and things like deferCurrentTask
        // it is thread safe because we already registered the listener so we won't miss any events
        if (runBeforeWaiting != null) runBeforeWaiting.run();

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
                Core.getInstance().eventRegistry.removeListener(event, listener);
                throw new RuntimeException("Error thrown in filter", ex);
            } finally {
                lock2.release();
            }
        }

        // returns new context and event value to the user so they can release joined stuff early
        return new EventAndContext(ev[0], ctxCont[0]);
    }
    
    /**
     * 
     * @since 1.2.3
     * 
     * @param event
     * @return a list of script-added listeners.
     */
    public List<IEventListener> listeners(String event) {
        List<IEventListener> listeners = new ArrayList<>();
        for (IEventListener l : Core.getInstance().eventRegistry.getListeners(event)) {
            if (!(l instanceof BaseListener)) listeners.add(l);
        }
        return listeners;
    }
    
    /**
    * create a custom event object that can trigger a event. It's recommended to use 
    * {@link EventCustom#registerEvent()} to set up the event to be visible in the GUI.
    * 
    * @see BaseEventRegistry#addEvent(String)
    * 
     * @param eventName name of the event. please don't use an existing one... your scripts might not like that.
     *
     * @since 1.2.8
     *
     * @return
     */
    public EventCustom createCustomEvent(String eventName) {
        return new EventCustom(eventName);
    }
    
    public interface ScriptEventListener extends IEventListener {
        Thread getCreator();
        
        MethodWrapper<BaseEvent, EventContainer<?>, Object, ?> getWrapper();
    }

    public static class EventAndContext {
        public final BaseEvent event;
        public final EventContainer<?> context;

        public EventAndContext(BaseEvent event, EventContainer<?> context) {
            this.event = event;
            this.context = context;
        }

        public String toString() {
            return String.format("EventAndContext:{\"event\": %s, \"context\": %s}", event.toString(), context.toString());
        }
    }
}
