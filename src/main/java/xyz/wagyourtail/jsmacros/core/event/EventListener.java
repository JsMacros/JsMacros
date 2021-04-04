package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

import java.util.concurrent.Semaphore;

public class EventListener extends BaseListener {
    
    public EventListener(ScriptTrigger macro, Core runner) {
        super(macro, runner);
    }
    
    @Override
    public Pair<? extends ScriptContext<?>, Semaphore> trigger(BaseEvent event) {
        return runScript(event);
    }
    
}