package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventArmorChange implements IEvent {
    public final String slot;
    public final ItemStackHelper item;
    public final ItemStackHelper oldItem;
    
    public EventArmorChange(String slot, ItemStack item, ItemStack old) {
        this.slot = slot;
        this.item = new ItemStackHelper(item);
        this.oldItem = new ItemStackHelper(old);
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"slot\": %d}", this.getEventName(), slot);
    }
}
