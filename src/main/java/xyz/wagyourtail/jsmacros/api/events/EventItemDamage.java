package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventItemDamage implements IEvent {
    public final ItemStackHelper item;
    public final int damage;
    
    public EventItemDamage(ItemStack stack, int damage) {
        this.item = new ItemStackHelper(stack);
        this.damage = damage;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"item\": %d}", this.getEventName(), item.toString());
    }
}
