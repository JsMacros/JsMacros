package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IEventRegistry {
    
    /**
     * @since 1.1.2 [citation needed]
     * @param rawmacro
     */
    public void addRawMacro(ScriptTrigger rawmacro);
    
    /**
     * @since 1.2.3
     * @param event
     * @param listener
     */
    public void addListener(String event, IEventListener listener);
    
    /**
     * @since 1.2.3
     * @param event
     * @param listener
     * @return
     */
    public boolean removeListener(String event, IEventListener listener);
    
    /**
     * @since 1.2.3
     * @param listener
     * @return
     */
    public boolean removeListener(IEventListener listener);
    
    /**
     * @since 1.1.2 [citation needed]
     * @param rawmacro
     * @return
     */
    public boolean removeRawMacro(ScriptTrigger rawmacro);
    
    /**
     * @since 1.2.3
     * @return
     */
    public Map<String, Set<IEventListener>> getListeners();
    
    /**
     * @since 1.2.3
     * @param key
     * @return
     */
    public Set<IEventListener> getListeners(String key);
    
    /**
     * @see IEventTrigger
     * @since 1.1.2 [citation needed]
     * @return
     */
    public List<ScriptTrigger> getRawMacros();
    
    /**
     * @since 1.1.2 [citation needed]
     * @param eventName
     */
    public void addEvent(String eventName);
}
