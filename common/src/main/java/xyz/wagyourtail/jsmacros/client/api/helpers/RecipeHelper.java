package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
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
     * @since 1.3.1
     * @return
     */
    public String getId() {
        return base.getId().toString();
    }

    /**
     * get ingredients list
     * @since 1.8.3
     * @return
     */
    public List<List<ItemStackHelper>> getIngredients() {
        List<List<ItemStackHelper>> ingredients = new ArrayList<>();
        for (Ingredient in : base.getPreviewInputs()) {
            ingredients.add(Arrays.stream(in.getMatchingStacksClient()).map(ItemStackHelper::new).collect(Collectors.toList()));
        }
        return ingredients;
    }

    /**
     * @since 1.3.1
     * @return
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getOutput());
    }
    
    /**
     * @since 1.3.1
     * @param craftAll
     */
    public void craft(boolean craftAll) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        if ((mc.currentScreen instanceof ContainerScreen && ((ContainerScreen<?>) mc.currentScreen).getContainer().syncId == syncId) ||
            (mc.currentScreen == null && syncId == mc.player.playerContainer.syncId)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickRecipe(syncId, base, craftAll);
            return;
        }
        throw new AssertionError("Crafting Screen no longer open!");
    }
    
    public String toString() {
        return String.format("Recipe:{\"id\":\"%s\"}", base.getId().toString());
    }
    
}
