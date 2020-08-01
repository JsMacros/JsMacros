package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.IEventListener;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.reflector.OptionsHelper;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.thread;

public class jsMacrosFunctions extends Functions {

    public jsMacrosFunctions(String libName) {
        super(libName);
    }
    
    public jsMacrosFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    public Profile getProfile() {
        return jsMacros.profile;
    }

    public ConfigManager getConfig() {
        return jsMacros.config;
    }
    
    public OptionsHelper getGameOptions() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return new OptionsHelper(mc.options);
    }

    public Map<RawMacro, List<thread>> getRunningThreads() {
        return RunScript.threads;
    }

    public String mcVersion() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.getGameVersion();
    }

    public void runScript(String file) {
        runScript(file, null);
    }

    public void runScript(String file, Consumer<String> callback) {
        if (callback != null) {
            RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null, () -> {
                callback.accept(null);
            }, callback);
        } else {
            RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null);
        }
    }
    
    public void open(String s) {
        Util.getOperatingSystem().open(s);
    }

    public IEventListener on(String event, BiConsumer<String, Map<String, Object>> callback) {
        if (callback == null) return null;
        String tname = Thread.currentThread().getName();
        IEventListener listener = new IEventListener() {
            @Override
            public Thread trigger(String type, Map<String, Object> args) {
                callback.accept(type, args);
                return null;
            }

            @Override
            public String toString() {
                return String.format("EventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", tname, event);
            }
        };
        Profile.registry.addListener(event, listener);
        return listener;
    }
    
    public IEventListener once(String event, BiConsumer<String, Map<String, Object>> callback) {
        if (callback == null) return null;
        String tname = Thread.currentThread().getName();
        IEventListener listener = new IEventListener() {
            @Override
            public Thread trigger(String type, Map<String, Object> args) {
                Profile.registry.removeListener(this);
                callback.accept(type, args);
                return null;
            }
            @Override
            public String toString() {
                return String.format("EventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", tname, event);
            }
            
        };
        Profile.registry.addListener(event, listener);
        return listener;
    }
    
    public boolean off(String event, IEventListener listener) {
        return Profile.registry.removeListener(event, listener);
    }
    
    public boolean off(IEventListener listener) {
        return Profile.registry.removeListener(listener);
    }
    
    public List<IEventListener> listeners(String event) {
        List<IEventListener> listeners = new ArrayList<>();
        for (IEventListener l : Profile.registry.getListeners(event)) {
            if (!(l instanceof BaseMacro)) listeners.add(l);
        }
        return listeners;
    }
    
    public String getFPS() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.fpsDebugString;
    }
}