package xyz.wagyourtail.jsmacros.client.api.events;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "ItemDamage", oldName = "ITEM_DAMAGE")
public class EventItemDamage implements BaseEvent {
    public final ItemStackHelper item;
    public final int damage;
    
    public EventItemDamage(ItemStack stack, int damage) {
        this.item = new ItemStackHelper(stack);
        this.damage = damage;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"item\": %d}", this.getEventName(), item.toString());
    }
}
