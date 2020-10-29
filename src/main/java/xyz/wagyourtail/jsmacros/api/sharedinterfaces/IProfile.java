package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import java.util.Map;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IProfile {

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
    public void triggerMacro(IEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacroJoin(IEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacroNoAnything(IEvent event);
    
    /**
     * @since 1.2.7
     * @param event
     */
    public void triggerMacroJoinNoAnything(IEvent event);
}
