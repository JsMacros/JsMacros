package xyz.wagyourtail.jsmacros.client.api.events;

import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon.Pos3D;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "SignEdit", oldName = "SIGN_EDIT")
public class EventSignEdit implements BaseEvent {
    public final Pos3D pos;
    public boolean closeScreen = false;
    public List<String> signText;
    
    public EventSignEdit(List<String> signText, int x, int y, int z) {
        this.pos = new Pos3D(x, y, z);
        this.signText = signText;
        
        profile.triggerEventJoinNoAnything(this);
    }
    
    public String toString() {
        return String.format("%s:{\"pos\": [%s]}", this.getEventName(), pos.toString());
    }
}
