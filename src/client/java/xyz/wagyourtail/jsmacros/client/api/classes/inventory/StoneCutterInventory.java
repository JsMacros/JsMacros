package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.recipe.display.SlotDisplayContexts;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class StoneCutterInventory extends Inventory<StonecutterScreen> {

    public StoneCutterInventory(StonecutterScreen inventory) {
        super(inventory);
    }

    /**
     * @return the selected recipe index.
     * @since 1.8.4
     */
    public int getSelectedRecipeIndex() {
        return inventory.getScreenHandler().getSelectedRecipe();
    }

    /**
     * @return the output item for the selected recipe.
     * @since 1.8.4
     */
    public ItemStackHelper getOutput() {
        return getSlot(1);
    }

    /**
     * @param idx the index to select
     * @return self for chaining.
     * @since 1.8.4
     */
    public StoneCutterInventory selectRecipe(int idx) {
        if (idx >= 0 && idx < inventory.getScreenHandler().getAvailableRecipeCount()) {
            inventory.getScreenHandler().onButtonClick(mc.player, idx);
            MinecraftClient.getInstance().interactionManager.clickButton(getCurrentSyncId(), idx);
        }
        return this;
    }

    /**
     * @return the amount of available recipes.
     * @since 1.8.4
     */
    public int getAvailableRecipeCount() {
        return inventory.getScreenHandler().getAvailableRecipeCount();
    }

    /**
     * @return a list of all available recipe results in the form of item stacks.
     * @since 1.8.4
     */
    public List<ItemStackHelper> getRecipes() {
        var context = SlotDisplayContexts.createParameters(mc.world);
        return inventory.getScreenHandler().getAvailableRecipes().entries().stream().map(recipe ->
                new ItemStackHelper(recipe.recipe().optionDisplay().getFirst(context))
                ).collect(Collectors.toList());
    }

    /**
     * @return {@code true} if there is a selected recipe, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canCraft() {
        return inventory.getScreenHandler().canCraft();
    }

    @Override
    public String toString() {
        return String.format("StoneCutterInventory:{}");
    }

}
