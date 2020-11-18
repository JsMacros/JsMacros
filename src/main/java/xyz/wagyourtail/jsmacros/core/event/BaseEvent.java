package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.IProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;

public interface BaseEvent {
    static final IProfile profile = ConfigManager.PROFILE;
    
    default String getEventName() {
        return "Event"+this.getClass().getAnnotation(Event.class).value();
    }
}
