package xyz.wagyourtail.jsmacros.api.events;

import xyz.wagyourtail.jsmacros.api.sharedclasses.PositionCommon.Pos3D;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventSound implements IEvent {
    public final String sound;
    public final float volume;
    public final float pitch;
    public final Pos3D position;
    
    public EventSound(String sound, float volume, float pitch, double x, double y, double z) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.position = new Pos3D(x, y, z);
        
        profile.triggerMacro(this);
    }

    public String toString() {
        return String.format("%s:{\"sound\": \"%s\"}", this.getEventName(), sound);
    }
}
