package xyz.wagyourtail.jsmacros.core;

import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;

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
    public IEventTrigger getRawMacro();
    
    /**
     * @since 1.2.7
     * @return
     */
    public long getStartTime();
}
