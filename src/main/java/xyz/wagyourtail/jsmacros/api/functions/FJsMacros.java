package xyz.wagyourtail.jsmacros.api.functions;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.network.ServerAddress;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.api.events.EventCustom;
import xyz.wagyourtail.jsmacros.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.core.*;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.IEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.events.TickSync;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Functions I didn't know where to put or interact directly with JsMacros or Events.
 * 
 * An instance of this class is passed to scripts as the {@code jsmacros} variable.
 * 
 * @author Wagyourtail
 */
 @Library("jsmacros")
public class FJsMacros implements BaseLibrary {
    
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Don't touch this plz xd.
     */
    public static TickSync tickSynchronizer = new TickSync();
    /**
     * @return the raw minecraft client class, it may be useful to use <a href="https://wagyourtail.xyz/Projects/Minecraft%20Mappings%20Viewer/App">Minecraft Mappings Viewer</a> for this.
     */
    public MinecraftClient getMinecraft() {
        return mc;
    }

    /**
     * @return the JsMacros profile class.
     */
    public IProfile getProfile() {
        return ConfigManager.PROFILE;
    }

    /**
     * @return the JsMacros config class.
     */
    public ConfigManager getConfig() {
        return ConfigManager.INSTANCE;
    }
    
    /**
     * @see xyz.wagyourtail.jsmacros.api.helpers.OptionsHelper
     * 
     * @since 1.1.7
     * 
     * @return an {@link xyz.wagyourtail.jsmacros.api.helpers.OptionsHelper OptionsHelper} for the game options.
     */
    public OptionsHelper getGameOptions() {
        return new OptionsHelper(mc.options);
    }

    /**
     * @return a {@link Map} of the current running threads.
     * 
     * @since 1.0.5
     * 
     */
    public Map<IEventTrigger, List<IScriptThreadWrapper>> getRunningThreads() {
        return ImmutableMap.copyOf(RunScript.threads);
    }

    /**
     * @return the current minecraft version as a {@link java.lang.String String}.
     * 
     * @since 1.1.2
     */
    public String mcVersion() {
        return mc.getGameVersion();
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
            return RunScript.exec(new ScriptTrigger(IEventTrigger.TriggerType.EVENT, "", file, true), null, () -> {
                callback.accept(null);
            }, callback);
        } else {
            return RunScript.exec(new ScriptTrigger(IEventTrigger.TriggerType.EVENT, "", file, true), null);
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
            ILanguage lang = RunScript.defaultLang;
            for (ILanguage l : RunScript.languages) {
                if (language.equals(l.extension().replaceAll("\\.", " ").trim().replaceAll(" ", "."))) {
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
        Util.getOperatingSystem().open(new File(ConfigManager.INSTANCE.macroFolder, path));
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
        String tname = Thread.currentThread().getName();
        IEventListener listener = new IEventListener() {
            
            @Override
            public Thread trigger(BaseEvent event) {
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(event);
                    } catch (Exception e) {
                        RunScript.eventRegistry.removeListener(this);
                        e.printStackTrace();
                    }
                });
                t.start();
                return t;
            }

            @Override
            public String toString() {
                return String.format("EventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", tname, event);
            }
        };
        RunScript.eventRegistry.addListener(event, listener);
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
        String tname = Thread.currentThread().getName();
        Thread th = Thread.currentThread();
        IEventListener listener = new IEventListener() {
            @Override
            public Thread trigger(BaseEvent event) {
                RunScript.eventRegistry.removeListener(this);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        while(th.isAlive());
                        callback.accept(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                return t;
            }
            @Override
            public String toString() {
                return String.format("OnceEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", tname, event);
            }
            
        };
        RunScript.eventRegistry.addListener(event, listener);
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
        return RunScript.eventRegistry.removeListener(listener);
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
        return RunScript.eventRegistry.removeListener(event, listener);
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
        Set<IEventListener> raw = RunScript.eventRegistry.getListeners(event);
        if (raw == null) return null;
        for (IEventListener l : ImmutableList.copyOf(raw)) {
            if (!(l instanceof BaseMacro)) listeners.add(l);
        }
        return listeners;
    }
    
    /**
     * @since 1.2.0
     * 
     * @return the fps debug string from minecraft.
     * 
     */
    public String getFPS() {
        return mc.fpsDebugString;
    }
    
    /**
     * @since 1.2.3
     * 
     * @see FJsMacros#connect(String, int)
     * 
     * @param ip
     */
    public void connect(String ip) {
        ServerAddress a = ServerAddress.parse(ip);
        connect(a.getAddress(), a.getPort());
    }
    
    /**
     * Connect to a server
     * 
     * @since 1.2.3
     * 
     * @param ip
     * @param port
     */
    public void connect(String ip, int port) {
        mc.execute(() -> {
            if (mc.world != null) mc.world.disconnect();
            mc.joinWorld(null);
            mc.openScreen(new ConnectScreen(null, mc, ip, port));
        });
    }

    /**
     * @since 1.2.3
     * 
     * @see FJsMacros#disconnect(MethodWrapper)
     */
    public void disconnect() {
        disconnect(null);
    }

    /**
     * Disconnect from a server with callback.
     * 
     * @since 1.2.3
     * 
     * {@code callback} defaults to {@code null}
     * 
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link java.lang.Boolean Boolean}&gt;
     */
    public void disconnect(MethodWrapper<Boolean, Object, Object> callback) {
        mc.execute(() -> {
            boolean isWorld = mc.world != null;
            if (isWorld) mc.world.disconnect();
            if (callback != null) callback.accept(isWorld);
        });
    }
    
    /**
     * @since 1.2.4
     * 
     * @see FJsMacros#waitTick(int)
     * 
     * @throws InterruptedException
     */
    public void waitTick() throws InterruptedException {
        tickSynchronizer.waitTick();
    }
    
    /**
     * waits the specified number of client ticks.
     * 
     * @since 1.2.6
     * 
     * @param i
     * @throws InterruptedException
     */
    public void waitTick(int i) throws InterruptedException {
        while (--i >= 0) {
            tickSynchronizer.waitTick();
        }
    }
    
    /**
    * create a custom event object that can trigger a event. It's recommended to use 
    * {@code jsMacros.getProfile().getRegistry().addEvent(eventName)} to set up the event to be visible in the GUI first.
    * 
    * @see IEventRegistry#addEvent(String)
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
}