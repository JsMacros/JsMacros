package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IScriptThreadWrapper {
    
    /**
     * @since 1.2.7
     * @return
     */
    public Thread getThread();
    
    /**
     * @since 1.2.7
     * @return
     */
    public IRawMacro getRawMacro();
    
    /**
     * @since 1.2.7
     * @return
     */
    public long getStartTime();
}
