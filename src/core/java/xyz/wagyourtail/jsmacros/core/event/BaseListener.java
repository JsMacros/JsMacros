package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

/**
 * This is for java-sided listeners, for creating listeners script sided directly use {@link IEventListener}
 */
public abstract class BaseListener implements IEventListener {
    protected final ScriptTrigger trigger;
    protected final Core runner;

    public BaseListener(ScriptTrigger trigger, Core runner) {
        this.trigger = trigger;
        this.runner = runner;
    }

    public ScriptTrigger getRawTrigger() {
        return trigger;
    }

    public EventContainer<?> runScript(BaseEvent event) {
        if (trigger.enabled) {
            try {
                return runner.exec(trigger, event);
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean joined() {
        return trigger.joined;
    }

    public boolean equals(Object o) {
        if (o instanceof BaseListener) {
            return trigger.equals(((BaseListener) o).trigger);
        }
        return super.equals(o);
    }

    @Override
    public void off() {
        trigger.enabled = false;
    }

    public String toString() {
        return trigger.toString().substring(3);
    }

}
