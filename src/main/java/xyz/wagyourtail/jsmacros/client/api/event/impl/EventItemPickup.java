package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "ItemPickup", oldName = "ITEM_PICKUP")
public class EventItemPickup implements BaseEvent {
    public final ItemStackHelper item;
    
    public EventItemPickup(ItemStack item) {
        this.item = new ItemStackHelper(item);
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"item\": %s}", this.getEventName(), item.toString());
    }
}
