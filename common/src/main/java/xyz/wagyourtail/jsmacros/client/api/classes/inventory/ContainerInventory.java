package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class ContainerInventory<T extends HandledScreen<?>> extends Inventory<T> {

    public ContainerInventory(T inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int findFreeContainerSlot() {
        return findFreeSlot("container");
    }
    
}
