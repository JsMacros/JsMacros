package xyz.wagyourtail.jsmacros.core.config;

import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;
import xyz.wagyourtail.jsmacros.core.library.impl.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public abstract class BaseProfile {
    protected final Core runner;
    public final Logger LOGGER;
    public final Set<Thread> joinedThreadStack = new HashSet<>();
    public String profileName;
    
    public BaseProfile(Core runner, Logger logger) {
        this.runner = runner;
        this.LOGGER = logger;
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
     * @since 1.6.0
     */
    public abstract boolean checkJoinedThreadStack();

    /**
     * @since 1.1.2 [citation needed]
     * @param profileName
     */
    public void loadOrCreateProfile(String profileName) {
        runner.eventRegistry.clearMacros();
        if (runner.config.getOptions(CoreConfigV2.class).profiles.containsKey(profileName)) {
            loadProfile(profileName);
        } else {
            runner.config.getOptions(CoreConfigV2.class).profiles.put(profileName, new ArrayList<>());
            loadProfile(profileName);
            runner.config.saveConfig();
        }
    }
    
    /**
    *
    * @since 1.0.3 [citation needed]
     * @param pName
     *
     * @return
     */
    protected boolean loadProfile(String pName) {
        runner.eventRegistry.clearMacros();
        final List<ScriptTrigger> rawProfile = runner.config.getOptions(CoreConfigV2.class).profiles.get(pName);
        if (rawProfile == null) {
            LOGGER.warn("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        renameCurrentProfile(pName);
        for (ScriptTrigger rawmacro : rawProfile) {
            runner.eventRegistry.addScriptTrigger(rawmacro);
        }
        new EventProfileLoad(this, pName);
        
        return true;
    }
    
    /**
     * @since 1.0.8 [citation needed]
     */
    public void saveProfile() {
        runner.config.getOptions(CoreConfigV2.class).profiles.put(getCurrentProfileName(), runner.eventRegistry.getScriptTriggers());
        runner.config.saveConfig();
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerEvent(BaseEvent event) {
        triggerEventNoAnything(event);
    
        for (IEventListener macro : runner.eventRegistry.getListeners("ANYTHING")) {
            macro.trigger(event);
        }
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public abstract void triggerEventJoin(BaseEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerEventNoAnything(BaseEvent event) {
        if (event instanceof EventCustom) {
            for (IEventListener macro : runner.eventRegistry.getListeners(((EventCustom) event).eventName)) {
                macro.trigger(event);
            }
        } else {
            for (IEventListener macro : runner.eventRegistry.getListeners(event.getEventName())) {
                macro.trigger(event);
            }
        }
    }
    
    /**
     * @since 1.2.7
     * @param event
     */
    public abstract void triggerEventJoinNoAnything(BaseEvent event);
    
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
        runner.libraryRegistry.addLibrary(FFS.class);
        runner.libraryRegistry.addLibrary(FGlobalVars.class);
        runner.libraryRegistry.addLibrary(FReflection.class);
        runner.libraryRegistry.addLibrary(FRequest.class);
    }
}
