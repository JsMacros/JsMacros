package xyz.wagyourtail.jsmacros.client.api.event.impl.inventory;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "ItemDamage", oldName = "ITEM_DAMAGE")
public class EventItemDamage extends BaseEvent {
    public final ItemStackHelper item;
    public final int damage;

    public EventItemDamage(ItemStack stack, int damage) {
        this.item = new ItemStackHelper(stack);
        this.damage = damage;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"item\": %s}", this.getEventName(), item.toString());
    }

}
