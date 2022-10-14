package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.util.DamageSource;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author FlareStormGaming
 * @since 1.6.5
 */
 @Event("Heal")
public class EventHeal implements BaseEvent {
    public final String source;
    public final float health;
    public final float change;

    public EventHeal(DamageSource source, float health, float change) {
        this.source = source.getName();
        this.health = health;
        this.change = change;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"health\": %f, \"change\": %f}", this.getEventName(), health, change);
    }
}
