package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.item.ItemStack;
import xyz.wagyourtail.doclet.DocletDeclareType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "ArmorChange", oldName = "ARMOR_CHANGE")
public class EventArmorChange extends BaseEvent {
    @DocletReplaceReturn("ArmorSlot")
    @DocletDeclareType(name = "ArmorSlot", type = "'HEAD' | 'CHEST' | 'LEGS' | 'FEET'")
    public final String slot;
    public final ItemStackHelper item;
    public final ItemStackHelper oldItem;

    public EventArmorChange(String slot, ItemStack item, ItemStack old) {
        this.slot = slot;
        this.item = new ItemStackHelper(item);
        this.oldItem = new ItemStackHelper(old);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"slot\": %s}", this.getEventName(), slot);
    }

}
