package xyz.wagyourtail.jsmacros.core.service;

import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.io.File;
import java.util.Objects;

public class ServiceTrigger {
    public File file;
    public boolean enabled;

    public ServiceTrigger(File file, boolean enabled) {
        this.file = file;
        this.enabled = enabled;
    }

    public ScriptTrigger toScriptTrigger() {
        return new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, EventService.class.getAnnotation(Event.class).value(), file, enabled, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceTrigger)) {
            return false;
        }
        ServiceTrigger that = (ServiceTrigger) o;
        return enabled == that.enabled && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, enabled);
    }

}
