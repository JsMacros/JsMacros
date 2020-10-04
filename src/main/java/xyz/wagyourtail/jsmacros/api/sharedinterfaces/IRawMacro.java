package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import xyz.wagyourtail.jsmacros.macros.MacroEnum;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IRawMacro {
    
    /**
     * @since 1.2.7
     * @return
     */
    public MacroEnum getType();
    
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
}
