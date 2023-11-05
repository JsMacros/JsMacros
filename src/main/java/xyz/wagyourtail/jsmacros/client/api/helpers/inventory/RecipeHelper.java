package xyz.wagyourtail.jsmacros.client.api.helpers.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.Registries;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class RecipeHelper extends BaseHelper<Recipe<?>> {
    protected int syncId;

    public RecipeHelper(Recipe<?> base, int syncId) {
        super(base);
        this.syncId = syncId;
    }

    /**
     * @return
     * @since 1.3.1
     */
    public String getId() {
        return base.getId().toString();
    }

    /**
     * get ingredients list
     *
     * @return
     * @since 1.8.3
     */
    public List<List<ItemStackHelper>> getIngredients() {
        List<List<ItemStackHelper>> ingredients = new ArrayList<>();
        for (Ingredient in : base.getIngredients()) {
            ingredients.add(Arrays.stream(in.getMatchingStacks()).map(ItemStackHelper::new).collect(Collectors.toList()));
        }
        return ingredients;
    }

    /**
     * @return
     * @since 1.3.1
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getOutput(MinecraftClient.getInstance().getNetworkHandler().getRegistryManager()));
    }

    /**
     * @param craftAll
     * @since 1.3.1
     */
    public RecipeHelper craft(boolean craftAll) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if ((mc.currentScreen instanceof HandledScreen && ((HandledScreen<?>) mc.currentScreen).getScreenHandler().syncId == syncId) ||
                (mc.currentScreen == null && syncId == mc.player.playerScreenHandler.syncId)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickRecipe(syncId, base, craftAll);
            return this;
        }
        throw new AssertionError("Crafting Screen no longer open!");
    }

    /**
     * @return the type of this recipe.
     * @since 1.8.4
     */
    public String getGroup() {
        return base.getGroup();
    }

    /**
     * This will not account for the actual items used in the recipe, but only the default recipe
     * itself. Items with durability or with a lot of tags will probably not work correctly.
     *
     * @return will return {@code true} if any of the default ingredients have a recipe remainder.
     * @since 1.8.4
     */
    public boolean hasRecipeRemainders() {
        return base.getIngredients().stream().anyMatch(ingredient -> ingredient.getMatchingStacks()[0].getItem().hasRecipeRemainder());
    }

    /**
     * @return a list of all possible recipe remainders.
     * @since 1.8.4
     */
    public List<List<ItemStackHelper>> getRecipeRemainders() {
        return base.getIngredients().stream()
                .filter(ingredient -> ingredient.getMatchingStacks().length > 0 && ingredient.getMatchingStacks()[0].getItem().hasRecipeRemainder())
                .map(ingredient -> Arrays.stream(ingredient.getMatchingStacks()).map(ItemStackHelper::new).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * @return the type of this recipe.
     * @since 1.8.4
     */
    public String getType() {
        return Registries.RECIPE_TYPE.getId(base.getType()).toString();
    }

    /**
     * @return {@code true} if the recipe can be crafted with the current inventory, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canCraft() {
        RecipeMatcher matcher = new RecipeMatcher();
        MinecraftClient.getInstance().player.getInventory().populateRecipeFinder(matcher);
        return matcher.match(base, null);
    }

    /**
     * @param amount the amount of items to craft
     * @return {@code true} if the given amount of items can be crafted with the current inventory,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canCraft(int amount) {
        return getCraftableAmount() >= amount;
    }

    /**
     * @return how often the recipe can be crafted with the current player inventory.
     * @since 1.8.4
     */
    public int getCraftableAmount() {
        RecipeMatcher matcher = new RecipeMatcher();
        MinecraftClient.getInstance().player.getInventory().populateRecipeFinder(matcher);
        return matcher.countCrafts(base, null);
    }

    @Override
    public String toString() {
        return String.format("RecipeHelper:{\"id\": \"%s\"}", base.getId().toString());
    }

}
