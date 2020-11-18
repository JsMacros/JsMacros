package xyz.wagyourtail.jsmacros.core.event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IEventTrigger {
    
    /**
     * @since 1.2.7
     * @return
     */
    public TriggerType getTriggerType();
    
    /**
     * @since 1.2.7
     * @return
     */
    public String getEvent();
    
    /**
     * @since 1.2.7
     * @return
     */
    public String getScriptFile();
    
    /**
     * @since 1.2.7
     * @return
     */
    public boolean getEnabled();
    
    /**
     * @since 1.2.7
     * @return
     */
    public IEventTrigger copy();

    /**
     * @since 1.0.0 [citation needed]
     * @author Wagyourtail
     */
    enum TriggerType {
        KEY_FALLING,
        KEY_RISING,
        KEY_BOTH,
        EVENT
    }
}
