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
     * @param name the new item name
     * @since 1.8.4
     */
    public void rename(String name) {
        ((IAnvilScreen) inventory).jsmacros_rename(name);
    }

    /**
     * @return the currently set name to be applied.
     *
     * @since 1.8.4
     */
    public String getRenameText() {
        return ((IAnvilScreen) inventory).jsmacros_getRenameText().getText();
    }

    /**
     * @param name the new item name
     * @since 1.8.4
     */
    public void setRenameText(String name) {
        ((IAnvilScreen) inventory).jsmacros_getRenameText().setText(name);
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
     * @return the amount of item needed to fully repair the item.
     *
     * @since 1.8.4
     */
    public int getItemRepairCost() {
        return getSlot(0).getRepairCost();
    }

    /**
     * @return the maximum default level cost.
     *
     * @since 1.8.4
     */
    public int getMaximumLevelCost() {
        return 40;
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
