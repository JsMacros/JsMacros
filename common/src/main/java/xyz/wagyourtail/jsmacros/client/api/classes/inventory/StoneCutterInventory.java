package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class StoneCutterInventory extends Inventory<StonecutterScreen> {

    public StoneCutterInventory(StonecutterScreen inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getSelectedRecipeIndex() {
        return inventory.getScreenHandler().getSelectedRecipe();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public void selectRecipe(int idx) {
        if (idx >= 0 && idx < inventory.getScreenHandler().getAvailableRecipeCount()) {
            inventory.getScreenHandler().onButtonClick(mc.player, idx);
            MinecraftClient.getInstance().interactionManager.clickButton(getCurrentSyncId(), idx);
        }
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getAvailableRecipeCount() {
        return inventory.getScreenHandler().getAvailableRecipeCount();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<ItemStackHelper> getRecipes() {
        return inventory.getScreenHandler().getAvailableRecipes().stream().map(recipe -> new ItemStackHelper(recipe.getOutput())).toList();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean canCraft() {
        return inventory.getScreenHandler().canCraft();
    }

}
