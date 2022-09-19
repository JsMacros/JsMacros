package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.SmithingScreen;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class SmithingInventory extends Inventory<SmithingScreen> {

    public SmithingInventory(SmithingScreen inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getFirstInput() {
        return getSlot(0);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getSecondInput() {
        return getSlot(1);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

}
