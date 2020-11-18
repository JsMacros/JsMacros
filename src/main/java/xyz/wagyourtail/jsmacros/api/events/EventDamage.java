package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.entity.damage.DamageSource;
import xyz.wagyourtail.jsmacros.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Damage", oldName = "DAMAGE")
public class EventDamage implements BaseEvent {
    public final EntityHelper attacker;
    public final String source;
    public final float health;
    public final float change;
    
    public EventDamage(DamageSource source, float health, float change) {
        if (source.getAttacker() == null) this.attacker = null;
        else this.attacker = EntityHelper.create(source.getAttacker());
        this.source = source.getName();
        this.health = health;
        this.change = change;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"health\": %f}", this.getEventName(), health);
    }
}
