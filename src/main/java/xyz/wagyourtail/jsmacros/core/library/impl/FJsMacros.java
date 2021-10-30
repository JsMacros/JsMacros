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
        return runScript(file, (MethodWrapper<Throwable, Object, Object, ?>) null);
    }

    /**
     * Run a script with optional callback of error.
     * 
     * @since 1.1.5
     * 
     * @param file relative to the macro folder.
     * @param callback defaults to {@code null}
     * @return the {@link EventContainer} the script is running on.
     */
    public EventContainer<?> runScript(String file, MethodWrapper<Throwable, Object, Object, ?> callback) {
        if (callback != null) {
            return Core.getInstance().exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", Core.getInstance().config.macroFolder.getAbsoluteFile().toPath().resolve(file).toFile(), true), null, () -> callback.accept(null), callback);
        } else {
            return Core.getInstance().exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", Core.getInstance().config.macroFolder.getAbsoluteFile().toPath().resolve(file).toFile(), true), null, null, null);
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
                    } catch (Exception ex) {
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
                    } catch (Exception ex) {
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

        // event return values
        final Exception[] e = {null};
        final BaseEvent[] ev = {null};

        // create a new event container so we can actually release joined events
        EventContainer<?> ctxCont = new EventContainer<>(ctx);

        // create the listener
        IEventListener listener = new ScriptEventListener() {
            @Override
            public EventContainer<?> trigger(BaseEvent event) {
                try {
                    // check the filter
                    boolean test = filter == null || filter.test(event);

                    if (test) {
                        // remove the listener here, since it this isn't async.
                        Core.getInstance().eventRegistry.removeListener(event.getEventName(), this);

                        // release the lock
                        lock.release();

                        ev[0] = event;
                        return ctxCont;
                    }
                } catch (Exception ex) {
                    // release the lock and set error
                    lock.release();
                    e[0] = ex;
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
        if (runBeforeWaiting != null) runBeforeWaiting.run();

        // make sure the current context isn't still locked.
        ctx.releaseBoundEventIfPresent(th);

        // set the new EventContainer's lock
        ctxCont.setLockThread(th);
        ctx.bindEvent(th, (EventContainer) ctxCont);

        // waits for event
        try {
            lock.acquire();
        } finally {
            Core.getInstance().eventRegistry.removeListener(event, listener);
        }
        if (e[0] != null) {
            throw new RuntimeException("Error thrown in filter", e[0]);
        }

        // returns new context and event value to the user so they can release joined stuff early
        return new EventAndContext(ev[0], ctxCont);
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
