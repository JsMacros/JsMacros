package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Functions that interact directly with JsMacros or Events.
 * 
 * An instance of this class is passed to scripts as the {@code jsmacros} variable.
 * 
 * @author Wagyourtail
 */
 @Library("jsmacros")
 @SuppressWarnings("unused")
public class FJsMacros extends BaseLibrary {
    
    /**
     * @return the JsMacros profile class.
     */
    public BaseProfile getProfile() {
        return Core.instance.profile;
    }

    /**
     * @return the JsMacros config management class.
     */
    public ConfigManager<?> getConfig() {
        return Core.instance.config;
    }
    
    

    /**
     * @return a {@link Map} of the current running threads.
     * 
     * @since 1.0.5
     * 
     */
    public Map<ScriptTrigger, List<ScriptThreadWrapper>> getRunningThreads() {
        return ImmutableMap.copyOf(Core.instance.threads);
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
    public Thread runScript(String file) {
        return runScript(file, (MethodWrapper<Throwable, Object, Object>) null);
    }

    /**
     * Run a script with optional callback of error.
     * 
     * @since 1.1.5
     * 
     * @param file relative to the macro folder.
     * @param callback defaults to {@code null}
     * @return the {@link java.lang.Thread} the script is running on.
     */
    public Thread runScript(String file, MethodWrapper<Throwable, Object, Object> callback) {
        if (callback != null) {
            return Core.instance.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", file, true), null, () -> callback.accept(null), callback);
        } else {
            return Core.instance.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", file, true), null);
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
    public Thread runScript(String language, String script) {
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
     * @return the {@link java.lang.Thread} the script is running on.
     */
    public Thread runScript(String language, String script, MethodWrapper<String, Object, Object> callback) {
        Thread t = new Thread(() -> {
            BaseLanguage lang = Core.instance.defaultLang;
            for (BaseLanguage l : Core.instance.languages) {
                if (language.equals(l.extension.replaceAll("\\.", " ").trim().replaceAll(" ", "."))) {
                    lang = l;
                    break;
                }
            }
            try {
                lang.exec(script, null, null);
                if (callback != null) callback.accept(null);
            } catch (Exception | AbstractMethodError e) {
                if (callback != null) callback.accept(e.toString());
            }
        });
        t.start();
        return t;
    }
    
    /**
     * Opens a file with the default system program.
     * 
     * @since 1.1.8
     * 
     * @param path relative to the macro folder.
     */
    public void open(String path) {
        Util.getOperatingSystem().open(new File(Core.instance.config.macroFolder, path));
    }
    
    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see IEventListener
     * 
     * @since 1.2.7
     * @param event
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link BaseEvent}&gt;
     * @return
     */
    public IEventListener on(String event, MethodWrapper<BaseEvent, Object, Object> callback) {
        if (callback == null) return null;
        Thread th = Thread.currentThread();
        IEventListener listener = new ScriptEventListener() {
            
            @Override
            public Thread trigger(BaseEvent event) {
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(event);
                    } catch (Exception e) {
                        Core.instance.eventRegistry.removeListener(this);
                        Core.instance.profile.logError(e);
                    }
                });
                t.start();
                return t;
            }
    
            @Override
            public Thread getCreator() {
                return th;
            }
    
            @Override
            public MethodWrapper<?, ?, ?> getWrapper() {
                return callback;
            }
    
            @Override
            public String toString() {
                return String.format("ScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
        };
        Core.instance.eventRegistry.addListener(event, listener);
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
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link BaseEvent}&gt;
     * @return the listener.
     */
    public IEventListener once(String event, MethodWrapper<BaseEvent, Object, Object> callback) {
        if (callback == null) return null;
        Thread th = Thread.currentThread();
        IEventListener listener = new ScriptEventListener() {
            @Override
            public Thread trigger(BaseEvent event) {
                Core.instance.eventRegistry.removeListener(this);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(event);
                    } catch (Exception e) {
                        Core.instance.profile.logError(e);
                    }
                });
                t.start();
                return t;
            }
    
            @Override
            public Thread getCreator() {
                return th;
            }
    
            @Override
            public MethodWrapper<?, ?, ?> getWrapper() {
                return callback;
            }
    
            @Override
            public String toString() {
                return String.format("OnceScriptEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", th.getName(), event);
            }
            
        };
        Core.instance.eventRegistry.addListener(event, listener);
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
        return Core.instance.eventRegistry.removeListener(listener);
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
        return Core.instance.eventRegistry.removeListener(event, listener);
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
        Set<IEventListener> raw = Core.instance.eventRegistry.getListeners(event);
        if (raw == null) return null;
        for (IEventListener l : ImmutableList.copyOf(raw)) {
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
        
        MethodWrapper<?,?,?> getWrapper();
    }
}