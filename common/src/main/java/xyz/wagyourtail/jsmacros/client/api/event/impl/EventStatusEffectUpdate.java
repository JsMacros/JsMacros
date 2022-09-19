package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Event(value = "StatusEffectUpdate")
public class EventStatusEffectUpdate implements BaseEvent {
    
    public final StatusEffectHelper effect;
    public final boolean added;
    public final boolean removed;

    public EventStatusEffectUpdate(StatusEffectHelper effect, boolean added) {
        this.effect = effect;
        this.added = added;
        this.removed = !added;
        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"effect\": \"%s\", \"%d\"}", this.getEventName(), effect.getId(), effect.getStrength());
    }
}
