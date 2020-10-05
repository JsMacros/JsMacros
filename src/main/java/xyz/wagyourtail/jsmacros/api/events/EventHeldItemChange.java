package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventHeldItemChange implements IEvent {
    public final boolean offHand;
    public final ItemStackHelper item;
    public final ItemStackHelper oldItem;
    
    public EventHeldItemChange(ItemStack item, ItemStack oldItem, boolean offHand) {
        this.item = new ItemStackHelper(item);
        this.oldItem = new ItemStackHelper(oldItem);
        this.offHand = offHand;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"item\": %s}", this.getEventName(), item.toString());
    }
}
