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
     */
    public void triggerMacro(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     */
    public void triggerMacroJoin(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     */
    public void triggerMacroNoAnything(String macroname, Map<String, Object> args);
    
    /**
     * @since 1.2.3 [citation needed]
     * @param macroname
     * @param args
     */
    public void triggerMacroJoinNoAnything(String macroname, Map<String, Object> args);
}
