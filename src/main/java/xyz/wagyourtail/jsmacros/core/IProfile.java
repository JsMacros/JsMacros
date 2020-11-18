package xyz.wagyourtail.jsmacros.core;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventRegistry;
import xyz.wagyourtail.jsmacros.core.library.impl.*;

import java.util.Map;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IProfile {
    
    public void logError(Throwable ex);

    /**
     * @since 1.1.2 [citation needed]
     * @return
     */
    public IEventRegistry getRegistry();
    
    /**
     * @since 1.1.2 [citation needed]
     * @param profileName
     */
    public void loadOrCreateProfile(String profileName);
    
    /**
     * @since 1.0.8 [citation needed]
     */
    public void saveProfile();
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     * @deprecated
     */
    public void triggerMacro(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     * @deprecated
     */
    public void triggerMacroJoin(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     * @deprecated
     */
    public void triggerMacroNoAnything(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     * @deprecated
     */
    public void triggerMacroJoinNoAnything(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacro(BaseEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacroJoin(BaseEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacroNoAnything(BaseEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacroJoinNoAnything(BaseEvent event);
    
    public void init(String defaultProfile);
    
    public String getCurrentProfileName();
    
    public void renameCurrentProfile(String profile);
    
    /**
     * Don't invoke from a script.
     */
    default void initRegistries() {
        RunScript.libraryRegistry.addLibrary(FConsumer.class);
        RunScript.libraryRegistry.addLibrary(FFS.class);
        RunScript.libraryRegistry.addLibrary(FGlobalVars.class);
        RunScript.libraryRegistry.addLibrary(FReflection.class);
        RunScript.libraryRegistry.addLibrary(FRequest.class);
    }
}
