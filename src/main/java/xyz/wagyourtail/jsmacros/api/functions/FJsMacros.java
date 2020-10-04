package xyz.wagyourtail.jsmacros.api.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.network.ServerAddress;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.api.Functions;
import xyz.wagyourtail.jsmacros.api.MethodWrappers;
import xyz.wagyourtail.jsmacros.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IConfig;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IProfile;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

/**
 * Functions I didn't know where to put or interact directly with JsMacros or Events.
 * 
 * An instance of this class is passed to scripts as the {@code jsmacros} variable.
 * 
 * @author Wagyourtail
 *
 */
public class FJsMacros extends Functions {
    /**
     * Don't touch this plz xd.
     */
    public static TickSync tickSynchronizer = new TickSync();

    public FJsMacros(String libName) {
        super(libName);
    }
    
    public FJsMacros(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
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
        return jsMacros.profile;
    }

    /**
     * @return the JsMacros config class.
     */
    public IConfig getConfig() {
        return jsMacros.config;
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
     * @return a {@link java.lang.Map} of the current running threads.
     * 
     * @since 1.0.5
     * 
     */
    public Map<IRawMacro, List<IScriptThreadWrapper>> getRunningThreads() {
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
     * @see xyz.wagyourtail.jsmacros.api.functions.FJsMacros#runScript(String, MethodWrappers.Consumer)
     * 
     * @since 1.1.5
     * 
     * @param file
     * @return
     */
    public Thread runScript(String file) {
        return runScript(file, (MethodWrappers.Consumer<String>) null);
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
    public Thread runScript(String file, MethodWrappers.Consumer<String> callback) {
        if (callback != null) {
            return RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null, () -> {
                callback.accept(null);
            }, callback);
        } else {
            return RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null);
        }
    }
    
    /**
     * @see xyz.wagyourtail.jsmacros.api.functions.FJsMacros#runScript(String, String, MethodWrappers.Consumer)
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
     * @param callback
     * @return the {@link java.lang.Thread} the script is running on.
     */
    public Thread runScript(String language, String script, MethodWrappers.Consumer<String> callback) {
        Thread t = new Thread(() -> {
            RunScript.Language lang = RunScript.defaultLang;
            for (RunScript.Language l : RunScript.languages) {
                if (language.equals(l.extension())) {
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
        Util.getOperatingSystem().open(new File(jsMacros.config.macroFolder, path));
    }

    /**
     * Creates a listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see xyz.wagyourtai.jsmacros.api.sharedinterfaces.IEventListener
     * 
     * @since 1.2.3
     * 
     * @param event
     * @param callback
     * @return the listener.
     */
    public IEventListener on(String event, MethodWrappers.BiConsumer<String, Map<String, Object>> callback) {
        if (callback == null) return null;
        String tname = Thread.currentThread().getName();
        IEventListener listener = new IEventListener() {
            
            @Override
            public Thread trigger(String type, Map<String, Object> args) {
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(type, args);
                    } catch (Exception e) {
                        Profile.registry.removeListener(this);
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
        Profile.registry.addListener(event, listener);
        return listener;
    }
    
    /**
     * Creates a single-run listener for an event, this function can be more efficient that running a script file when used properly.
     * 
     * @see xyz.wagyourtai.jsmacros.api.sharedinterfaces.IEventListener
     * 
     * @since 1.2.3
     * 
     * @param event
     * @param callback
     * @return the listener.
     */
    public IEventListener once(String event, MethodWrappers.BiConsumer<String, Map<String, Object>> callback) {
        if (callback == null) return null;
        String tname = Thread.currentThread().getName();
        Thread th = Thread.currentThread();
        IEventListener listener = new IEventListener() {
            @Override
            public Thread trigger(String type, Map<String, Object> args) {
                Profile.registry.removeListener(this);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        while(th.isAlive());
                        callback.accept(type, args);
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
        Profile.registry.addListener(event, listener);
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
        return Profile.registry.removeListener(listener);
    }
    
    /**
     * Removes a {@link xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener IEventListener} from an event.
     * 
     * @see xyz.wagyourtai.jsmacros.api.sharedinterfaces.IEventListener
     * 
     * @since 1.2.3
     * 
     * @param event
     * @param listener
     * @return
     */
    public boolean off(String event, IEventListener listener) {
        return Profile.registry.removeListener(event, listener);
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
        Set<IEventListener> raw = Profile.registry.getListeners(event);
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
     * @see FJsMacros#disconnect(MethodWrappers.Consumer)
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
     * @param callback
     */
    public void disconnect(MethodWrappers.Consumer<Boolean> callback) {
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
     * @author Wagyourtail
     * Ignore this xd
     */
    public static class TickSync {
        int tc = 0;
        public synchronized void waitTick() throws InterruptedException {
            int tcc = tc;
            while (tc == tcc) {
                this.wait();
            }
        }
        
        public synchronized void tick() {
            ++tc;
            this.notifyAll();
        }
    }
}