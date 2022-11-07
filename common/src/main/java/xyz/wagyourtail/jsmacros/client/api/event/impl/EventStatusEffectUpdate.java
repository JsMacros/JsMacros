package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "StatusEffectUpdate")
public class EventStatusEffectUpdate implements BaseEvent {

    public final StatusEffectHelper oldEffect;
    public final StatusEffectHelper newEffect;
    public final boolean added;
    public final boolean removed;

    public EventStatusEffectUpdate(StatusEffectHelper oldEffect, StatusEffectHelper newEffect, boolean added) {
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
        this.added = added;
        this.removed = !added;
        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"effect\": \"%s\", \"strength\": %d, \"time\": %d, \"change\": %s}", this.getEventName(), added ? newEffect.getId() : oldEffect.getId(), added ? newEffect.getStrength() : oldEffect.getStrength(), added ? newEffect.getTime() : oldEffect.getTime(), added ? "added" : "removed");
    }
}
