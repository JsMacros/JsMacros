package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.inventory.slot.Slot;

public interface IInventory {
    Slot jsmacros_getSlotUnder(int x, int y);
}
