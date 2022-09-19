package xyz.wagyourtail.jsmacros.client.api.event.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Event(value = "InventoryChange")
public class EventInventoryChange implements BaseEvent {
    public final Inventory<?> inventory;
    public final int[] slots;
    public final Map<Integer, ItemStackHelper> oldItems;
    public final Map<Integer, ItemStackHelper> newItems;

    public EventInventoryChange(Inventory<?> inventory, int[] slots, ItemStackHelper oldItems, ItemStackHelper newItems) {
        this(inventory, slots, new ItemStackHelper[]{oldItems}, new ItemStackHelper[]{newItems});
    }

    public EventInventoryChange(Inventory<?> inventory, int[] slots, ItemStackHelper[] oldItems, ItemStackHelper[] newItems) {
        this(inventory, slots, new Int2ObjectArrayMap<>(slots, oldItems), new Int2ObjectArrayMap<>(slots, newItems));
    }

    public EventInventoryChange(Inventory<?> inventory, int[] slots, Map<Integer, ItemStackHelper> oldItems, Map<Integer, ItemStackHelper> newItems) {
        this.inventory = inventory;
        this.slots = slots;
        this.oldItems = oldItems;
        this.newItems = newItems;
        
        profile.triggerEvent(this);
    }

}
