package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventItemPickup implements IEvent {
    public final ItemStackHelper item;
    
    public EventItemPickup(ItemStack item) {
        this.item = new ItemStackHelper(item);
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"item\": %s}", this.getEventName(), item.toString());
    }
}
