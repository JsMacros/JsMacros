package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SmithingInventory extends Inventory<SmithingScreen> {

    public SmithingInventory(SmithingScreen inventory) {
        super(inventory);
    }

    /**
     * @return the left input item.
     * @since 1.8.4
     */
    public ItemStackHelper getLeftInput() {
        return getSlot(0);
    }

    /**
     * @return the right input item.
     * @since 1.8.4
     */
    public ItemStackHelper getRightInput() {
        return getSlot(1);
    }

    /**
     * @return the expected output item.
     * @since 1.8.4
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

    @Override
    public String toString() {
        return String.format("SmithingInventory:{}");
    }

}
