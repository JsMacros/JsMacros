package xyz.wagyourtail.jsmacros.api.events;

import java.util.List;

import xyz.wagyourtail.jsmacros.api.sharedclasses.PositionCommon.Pos3D;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventSignEdit implements IEvent {
    public final Pos3D pos;
    public boolean closeScreen = false;
    public List<String> signText;
    
    public EventSignEdit(List<String> signText, int x, int y, int z) {
        this.pos = new Pos3D(x, y, z);
        this.signText = signText;
        
        profile.triggerMacroJoinNoAnything(this);
    }
    
    public String toString() {
        return String.format("%s:{\"pos\": [%s]}", this.getEventName(), pos.toString());
    }
}
