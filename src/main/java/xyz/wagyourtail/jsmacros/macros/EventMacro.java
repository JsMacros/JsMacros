package xyz.wagyourtail.jsmacros.macros;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class EventMacro extends BaseMacro {
    
    public EventMacro(RawMacro macro) {
        super(macro);
    }
    
    @Override
    public Thread trigger(IEvent event) {
        return runMacro(event);
    }
}