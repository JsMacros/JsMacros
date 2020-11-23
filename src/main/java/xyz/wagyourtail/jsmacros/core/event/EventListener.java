package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

public class EventListener extends BaseListener {
    
    public EventListener(ScriptTrigger macro, Core runner) {
        super(macro, runner);
    }
    
    @Override
    public Thread trigger(BaseEvent event) {
        return runScript(event);
    }
}