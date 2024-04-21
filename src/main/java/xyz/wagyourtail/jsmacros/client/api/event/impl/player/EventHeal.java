package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.entity.damage.DamageSource;
import xyz.wagyourtail.doclet.DocletDeclareType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author FlareStormGaming
 * @since 1.6.5
 */
@Event("Heal")
public class EventHeal extends BaseEvent {
    @DocletReplaceReturn("HealSource")
    @DocletDeclareType(name = "HealSource", type = "DamageSource")
    public final String source;
    public final float health;
    public final float change;

    public EventHeal(DamageSource source, float health, float change) {
        this.source = source.getName();
        this.health = health;
        this.change = change;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"health\": %f, \"change\": %f}", this.getEventName(), health, change);
    }

}
