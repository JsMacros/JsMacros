package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @since 1.5.0
 */
@Event("Riding")
public class EventRiding implements BaseEvent {
    public final boolean state;
    public final EntityHelper<?> entity;

    public EventRiding(boolean state, Entity entity) {
        this.state = state;
        this.entity = EntityHelper.create(entity);

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"state\": %b, \"entity\": %s}", this.getEventName(), state, entity.toString());
    }
}
