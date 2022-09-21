package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;

import xyz.wagyourtail.jsmacros.client.access.IAnvilScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AnvilInventory extends Inventory<AnvilScreen> {

    public AnvilInventory(AnvilScreen inventory) {
        super(inventory);
    }

    /**
     * @param name the new name
     * @since 1.8.4
     */
    public void rename(String name) {
        ((IAnvilScreen) inventory).jsmacros_rename(name);
    }

    /**
     * @return the level cost to apply the changes.
     *
     * @since 1.8.4
     */
    public int getLevelCost() {
        return inventory.getScreenHandler().getLevelCost();
    }

    /**
     * @return the first input item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getFirstInput() {
        return getSlot(0);
    }

    /**
     * @return the second input item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getSecondInput() {
        return getSlot(1);
    }

    /**
     * @return the expected output item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

}
