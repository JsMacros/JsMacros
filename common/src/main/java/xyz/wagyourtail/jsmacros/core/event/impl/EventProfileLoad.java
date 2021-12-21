package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "ProfileLoad", oldName = "PROFILE_LOAD")
public class EventProfileLoad implements BaseEvent {
    public final String profileName;
    
    public EventProfileLoad(BaseProfile profile, String profileName) {
        this.profileName = profileName;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"profileName\": %s}", this.getEventName(), profileName);
    }
}
