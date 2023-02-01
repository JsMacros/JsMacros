package xyz.wagyourtail.jsmacros.client.api.classes.inventory;


import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.recipe.RecipeType;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class CraftingInventory extends RecipeInventory<CraftingTableScreen> {

    protected CraftingInventory(CraftingTableScreen inventory) {
        super(inventory);
    }

    /**
     * @param x the x position of the input from 0 to 2, going left to right
     * @param y the y position of the input from 0 to 2, going top to bottom
     * @return the input item at the given position.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getInput(int x, int y) {
        return getSlot(x + y * 3 + 1);
    }

    @Override
    protected RecipeType<?> getRecipeType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public String toString() {
        return String.format("CraftingInventory:{}");
    }
    
}
