package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IProfile;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventProfileLoad implements IEvent {
    public final String profileName;
    
    public EventProfileLoad(IProfile profile, String profileName) {
        this.profileName = profileName;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"profileName\": %s}", this.getEventName(), profileName);
    }
}
