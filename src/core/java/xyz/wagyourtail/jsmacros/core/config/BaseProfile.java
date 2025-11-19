package xyz.wagyourtail.jsmacros.core.config;

import org.slf4j.Logger;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
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
    public final Core<?, ?> runner;
    public final Logger LOGGER;
    public final Set<Thread> joinedThreadStack = new HashSet<>();

    public String profileName;

    public BaseProfile(Core runner, Logger logger) {
        this.runner = runner;
        this.LOGGER = logger;
    }

    public abstract void logError(Throwable ex);

    /**
     * @return
     * @since 1.1.2 [citation needed]
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
     * @param profileName
     * @since 1.1.2 [citation needed]
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
     * @param pName
     * @return
     * @since 1.0.3 [citation needed]
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
        new EventProfileLoad(this, pName).trigger();

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
     * @param event
     * @since 1.2.7
     */
    public void triggerEvent(BaseEvent event) {
        boolean joinedMain = checkJoinedThreadStack();
        if (event instanceof EventCustom) {
            for (IEventListener macro : runner.eventRegistry.getListeners(((EventCustom) event).eventName)) {
                macro.trigger(event);
            }

            if (!runner.config.getOptions(CoreConfigV2.class).anythingIgnored.contains(((EventCustom) event).eventName)) {
                for (IEventListener macro : runner.eventRegistry.getListeners("ANYTHING")) {
                    if (macro.joined() && event.joinable()) {
                        runJoinedEventListener(event, joinedMain, macro);
                    } else {
                        macro.trigger(event);
                    }
                }
            }
        } else {
            String eventName = event.getEventName();
            for (IEventListener macro : runner.eventRegistry.getListeners(eventName)) {
                if (macro.joined() && runner.eventRegistry.joinableEvents.contains(eventName)) {
                    runJoinedEventListener(event, joinedMain, macro);
                } else {
                    macro.trigger(event);
                }
            }

            if (!runner.config.getOptions(CoreConfigV2.class).anythingIgnored.contains(eventName)) {
                for (IEventListener macro : runner.eventRegistry.getListeners("ANYTHING")) {
                    if (macro.joined() && runner.eventRegistry.joinableEvents.contains(eventName)) {
                        runJoinedEventListener(event, joinedMain, macro);
                    } else {
                        macro.trigger(event);
                    }
                }
            }
        }
    }

    protected void runJoinedEventListener(BaseEvent event, boolean joinedMain, IEventListener macroListener) {
        if (macroListener instanceof FJsMacros.ScriptEventListener) {
            BaseScriptContext<?> ctx = ((FJsMacros.ScriptEventListener) macroListener).getCtx();
            if (ctx != null && ctx.getBoundThreads().contains(Thread.currentThread()) && !ctx.isMultiThreaded()) {
                throw new IllegalThreadStateException("Cannot join " + macroListener + " on same context as it's creation.");
            }
        }
        EventContainer<?> t = macroListener.trigger(event);
        if (t == null) {
            return;
        }
        try {
            if (joinedMain) {
                joinedThreadStack.add(t.getLockThread());
                EventLockWatchdog.startWatchdog(t, macroListener, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
            }
            t.awaitLock(() -> joinedThreadStack.remove(t.getLockThread()));
        } catch (InterruptedException ignored) {
            joinedThreadStack.remove(t.getLockThread());
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
        runner.eventRegistry.addEvent("ANYTHING", true, true);
        runner.eventRegistry.addEvent(EventProfileLoad.class);

        runner.libraryRegistry.addLibrary(FJsMacros.class);
        runner.libraryRegistry.addLibrary(FFS.class);
        runner.libraryRegistry.addLibrary(FGlobalVars.class);
        runner.libraryRegistry.addLibrary(FReflection.class);
        runner.libraryRegistry.addLibrary(FRequest.class);
        runner.libraryRegistry.addLibrary(FTime.class);
    }

}
