package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.screen.AnvilScreenHandler;

import xyz.wagyourtail.jsmacros.client.access.IAnvilScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class AnvilInventory extends Inventory<AnvilScreen> {

    public AnvilInventory(AnvilScreen inventory) {
        super(inventory);
    }

    /**
     * @param name
     * @since 1.9.0
     */
    public void rename(String name) {
        ((IAnvilScreen) inventory).jsmacros_rename(name);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getLevelCost() {
        return inventory.getScreenHandler().getLevelCost();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getNextLevelCost() {
        return AnvilScreenHandler.getNextCost(getLevelCost());
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
