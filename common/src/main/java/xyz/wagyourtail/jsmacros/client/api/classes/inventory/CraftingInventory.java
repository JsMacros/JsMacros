package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.CraftingScreen;

import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class CraftingInventory extends RecipeInventory<CraftingScreen> {

    protected CraftingInventory(CraftingScreen inventory) {
        super(inventory);
    }

    /**
     * @param x the x position of the input from 0 to 2, going left to right.
     * @param y the y position of the input from 0 to 2, going top to bottom.
     * @return the input item at the given position.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getInput(int x, int y) {
        return getSlot(x + y * 3 + 1);
    }

    @Override
    public String toString() {
        return String.format("CraftingInventory:{}");
    }
    
}
