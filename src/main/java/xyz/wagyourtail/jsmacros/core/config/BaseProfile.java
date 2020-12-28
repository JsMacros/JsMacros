package xyz.wagyourtail.jsmacros.core.config;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;
import xyz.wagyourtail.jsmacros.core.library.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public abstract class BaseProfile {
    protected final Core runner;
    public String profileName;
    
    public BaseProfile(Core runner) {
        this.runner = runner;
    }
    
    public abstract void logError(Throwable ex);

    /**
     * @since 1.1.2 [citation needed]
     * @return
     */
     @Deprecated
    public BaseEventRegistry getRegistry() {
        return runner.eventRegistry;
    }
    
    /**
     * @since 1.1.2 [citation needed]
     * @param profileName
     */
    public void loadOrCreateProfile(String profileName) {
        runner.eventRegistry.clearMacros();
        if (runner.config.options.profiles.containsKey(profileName)) {
            loadProfile(profileName);
        } else {
            runner.config.options.profiles.put(profileName, new ArrayList<>());
            loadProfile(profileName);
            runner.config.saveConfig();
        }
    }
    
    /**
    *
    * @Since 1.0.3 [citation needed]
     * @param pName
     *
     * @return
     */
    public boolean loadProfile(String pName) {
        runner.eventRegistry.clearMacros();
        final List<ScriptTrigger> rawProfile = runner.config.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        renameCurrentProfile(pName);
        for (ScriptTrigger rawmacro : rawProfile) {
            runner.eventRegistry.addScriptTrigger(rawmacro);
        }
        Map<String, Object> args = new HashMap<>();
        args.put("profile", pName);
        new EventProfileLoad(this, pName);
        
        return true;
    }
    
    /**
     * @since 1.0.8 [citation needed]
     */
    public void saveProfile() {
        runner.config.options.profiles.put(getCurrentProfileName(), runner.eventRegistry.getScriptTriggers());
        runner.config.saveConfig();
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerEvent(BaseEvent event) {
        triggerEventNoAnything(event);
    
        if (runner.eventRegistry.listeners.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(runner.eventRegistry.listeners.get("ANYTHING"))) {
            macro.trigger(event);
        }
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerEventJoin(BaseEvent event) {
        triggerEventJoinNoAnything(event);
    
        if (runner.eventRegistry.listeners.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(runner.eventRegistry.listeners.get("ANYTHING"))) {
            try {
                Thread t = macro.trigger(event);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerEventNoAnything(BaseEvent event) {
        if (event instanceof EventCustom) {
            if (runner.eventRegistry.listeners.containsKey(((EventCustom) event).eventName))
                for (IEventListener macro : ImmutableList.copyOf(runner.eventRegistry.listeners.get(((EventCustom) event).eventName))) {
                    macro.trigger(event);
                }
        } else {
            if (runner.eventRegistry.listeners.containsKey(event.getEventName()))
                for (IEventListener macro : ImmutableList.copyOf(runner.eventRegistry.listeners.get(event.getEventName()))) {
                    macro.trigger(event);
                }
        }
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerEventJoinNoAnything(BaseEvent event) {
        if (event instanceof EventCustom) {
            if (runner.eventRegistry.listeners.containsKey(((EventCustom) event).eventName))
                for (IEventListener macro : ImmutableList.copyOf(runner.eventRegistry.listeners.get(((EventCustom) event).eventName))) {
                    try {
                        Thread t = macro.trigger(event);
                        if (t != null) t.join();
                    } catch (InterruptedException e) {
                    }
                }
        } else {
            if (runner.eventRegistry.listeners.containsKey(event.getEventName()))
                for (IEventListener macro : ImmutableList.copyOf(runner.eventRegistry.listeners.get(event.getEventName()))) {
                    try {
                        Thread t = macro.trigger(event);
                        if (t != null) t.join();
                    } catch (InterruptedException e) {
                    }
                }
        }
    }
    
    public void init(String defaultProfile) {
        initRegistries();
        loadOrCreateProfile(defaultProfile);
    }
    
    public String getCurrentProfileName() {
        return profileName;
    }
    
    public void renameCurrentProfile(String profile) {
        profileName = profile;
    }
    /**
     * Don't invoke from a script, extend to add more.
     */
    protected void initRegistries() {
        runner.eventRegistry.addEvent("ANYTHING");
        runner.eventRegistry.addEvent(EventProfileLoad.class);
    
        runner.libraryRegistry.addLibrary(FJsMacros.class);
        runner.libraryRegistry.addLibrary(FConsumer.class);
        runner.libraryRegistry.addLibrary(FFS.class);
        runner.libraryRegistry.addLibrary(FGlobalVars.class);
        runner.libraryRegistry.addLibrary(FReflection.class);
        runner.libraryRegistry.addLibrary(FRequest.class);
    }
}
