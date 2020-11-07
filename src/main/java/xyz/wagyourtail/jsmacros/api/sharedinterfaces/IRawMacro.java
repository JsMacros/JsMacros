package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IRawMacro {
    
    /**
     * @since 1.2.7
     * @return
     */
    public MacroType getType();
    
    /**
     * @since 1.2.7
     * @return
     */
    public String getEventKey();
    
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
    public IRawMacro copy();

    /**
     * @since 1.0.0 [citation needed]
     * @author Wagyourtail
     */
    enum MacroType {
        KEY_FALLING,
        KEY_RISING,
        KEY_BOTH,
        EVENT
    }
}
