package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

public class EventListener extends BaseListener {

    public EventListener(ScriptTrigger macro, Core runner) {
        super(macro, runner);
    }

    @Override
    public EventContainer<?> trigger(BaseEvent event) {
        return runScript(event);
    }

}
