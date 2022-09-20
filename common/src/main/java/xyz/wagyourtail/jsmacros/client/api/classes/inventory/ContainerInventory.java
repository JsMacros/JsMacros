package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class ContainerInventory<T extends HandledScreen<?>> extends Inventory<T> {

    public ContainerInventory(T inventory) {
        super(inventory);
    }

    /**
     * @return the first free slot in the container.
     *
     * @since 1.9.0
     */
    public int findFreeContainerSlot() {
        return findFreeSlot("container");
    }

}
