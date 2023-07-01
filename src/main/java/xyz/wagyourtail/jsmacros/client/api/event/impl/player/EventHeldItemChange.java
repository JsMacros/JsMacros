package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "HeldItemChange", oldName = "HELD_ITEM")
public class EventHeldItemChange implements BaseEvent {
    public final boolean offHand;
    public final ItemStackHelper item;
    public final ItemStackHelper oldItem;

    public EventHeldItemChange(ItemStack item, ItemStack oldItem, boolean offHand) {
        this.item = new ItemStackHelper(item);
        this.oldItem = new ItemStackHelper(oldItem);
        this.offHand = offHand;

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"item\": %s}", this.getEventName(), item.toString());
    }

}
