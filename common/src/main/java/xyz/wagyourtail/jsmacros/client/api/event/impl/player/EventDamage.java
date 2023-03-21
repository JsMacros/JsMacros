package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.entity.damage.DamageSource;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Damage", oldName = "DAMAGE")
public class EventDamage implements BaseEvent {
    public final EntityHelper<?> attacker;
    @DocletReplaceReturn("DamageSource")
    public final String source;
    public final float health;
    public final float change;
    
    public EventDamage(DamageSource source, float health, float change) {
        if (source.getAttacker() == null) this.attacker = null;
        else this.attacker = EntityHelper.create(source.getAttacker());
        this.source = source.getName();
        this.health = health;
        this.change = change;
        
        profile.triggerEvent(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s:{\"health\": %f, \"change\": %f}", this.getEventName(), health, change);
    }
}
