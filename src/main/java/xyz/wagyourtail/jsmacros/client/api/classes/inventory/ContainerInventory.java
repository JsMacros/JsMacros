package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ContainerInventory<T extends HandledScreen<?>> extends Inventory<T> {

    public ContainerInventory(T inventory) {
        super(inventory);
    }

    /**
     * @return the first free slot in this container.
     * @since 1.8.4
     */
    public int findFreeContainerSlot() {
        return findFreeSlot("container");
    }

    @Override
    public String toString() {
        return String.format("ContainerInventory:{}");
    }

}
