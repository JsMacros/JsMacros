package xyz.wagyourtail.jsmacros.macros;

import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

public class EventMacro extends BaseMacro {
    
    public EventMacro(ScriptTrigger macro) {
        super(macro);
    }
    
    @Override
    public Thread trigger(BaseEvent event) {
        return runMacro(event);
    }
}