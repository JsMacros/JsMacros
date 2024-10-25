package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinAnvilScreen;

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
     * @return the currently set name to be applied.
     * @since 1.8.4
     */
    public String getName() {
        return ((MixinAnvilScreen) inventory).getNameField().getText();
    }

    /**
     * The change will be applied once the item is taken out of the anvil.
     *
     * @param name the new item name
     * @return self for chaining.
     * @since 1.8.4
     */
    public AnvilInventory setName(String name) {
        ((MixinAnvilScreen) inventory).getNameField().setText(name);
        return this;
    }

    /**
     * @return the level cost to apply the changes.
     * @since 1.8.4
     */
    public int getLevelCost() {
        return inventory.getScreenHandler().getLevelCost();
    }

    /**
     * @return the amount of item needed to fully repair the item.
     * @since 1.8.4
     */
    public int getItemRepairCost() {
        return getSlot(0).getRepairCost();
    }

    /**
     * @return the maximum default level cost.
     * @since 1.8.4
     */
    public int getMaximumLevelCost() {
        return 40;
    }

    /**
     * @return the first input item.
     * @since 1.8.4
     */
    public ItemStackHelper getLeftInput() {
        return getSlot(0);
    }

    /**
     * @return the second input item.
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
        return String.format("AnvilInventory:{}");
    }

}
