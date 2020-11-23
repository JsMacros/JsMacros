package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.Core;

public interface BaseEvent {
    static final BaseProfile profile = Core.instance.profile;
    
    default String getEventName() {
        return this.getClass().getAnnotation(Event.class).value();
    }
}
