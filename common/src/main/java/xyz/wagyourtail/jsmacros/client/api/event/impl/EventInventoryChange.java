package xyz.wagyourtail.jsmacros.client.api.event.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Event(value = "InventoryChange")
public class EventInventoryChange implements BaseEvent {
    public final Inventory<?> inventory;
    public final int[] changedSlots;
    public final Map<Integer, ItemStackHelper> oldItems;
    public final Map<Integer, ItemStackHelper> newItems;

    public EventInventoryChange(Inventory<?> inventory, int[] changedSlots, ItemStackHelper oldItems, ItemStackHelper newItems) {
        this(inventory, changedSlots, new ItemStackHelper[]{oldItems}, new ItemStackHelper[]{newItems});
    }

    public EventInventoryChange(Inventory<?> inventory, int[] changedSlots, ItemStackHelper[] oldItems, ItemStackHelper[] newItems) {
        this(inventory, changedSlots, new Int2ObjectArrayMap<>(changedSlots, oldItems), new Int2ObjectArrayMap<>(changedSlots, newItems));
    }

    public EventInventoryChange(Inventory<?> inventory, int[] changedSlots, Map<Integer, ItemStackHelper> oldItems, Map<Integer, ItemStackHelper> newItems) {
        this.inventory = inventory;
        this.changedSlots = changedSlots;
        this.oldItems = oldItems;
        this.newItems = newItems;
        
        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"inventory\": %s, \"slots\": %s}", this.getEventName(), inventory, Arrays.toString(changedSlots));
    }

}
