package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;

public interface BaseEvent {
    BaseProfile profile = Core.getInstance().profile;

    default String getEventName() {
        return this.getClass().getAnnotation(Event.class).value();
    }

}
