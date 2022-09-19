package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class BrewingStandInventory extends Inventory<BrewingStandScreen> {

    public BrewingStandInventory(BrewingStandScreen inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBrewablePotion(ItemStackHelper item) {
        Potion potion = PotionUtil.getPotion(item.getRaw());
        if (potion != Potions.EMPTY) {
            return BrewingRecipeRegistry.isBrewable(potion);
        }
        return false;
    }

    /**
     * @param stack
     * @return
     *
     * @since 1.9.0
     */
    public boolean isValidIngredient(ItemStackHelper stack) {
        return BrewingRecipeRegistry.isValidIngredient(stack.getRaw());
    }

    /**
     * @param potion
     * @param ingredient
     * @return
     *
     * @since 1.9.0
     */
    public boolean isValidRecipe(ItemStackHelper potion, ItemStackHelper ingredient) {
        return BrewingRecipeRegistry.hasRecipe(potion.getRaw(), ingredient.getRaw());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getFuelCount() {
        return inventory.getScreenHandler().getFuel();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getMaxFuelUses() {
        return BrewingStandBlockEntity.field_31324;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean canBrewCurrentInput() {
        ItemStackHelper ingredient = getIngredient();
        if (ingredient.isEmpty()) {
            return false;
        } else if (!isValidIngredient(ingredient)) {
            return false;
        } else {
            for (ItemStackHelper stack : getPotions()) {
                if (!stack.isEmpty() && isValidRecipe(stack, ingredient)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getBrewTime() {
        return inventory.getScreenHandler().getBrewTime();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getRemainingTicks() {
        return BrewingRecipeRegistry.field_30942 * 20 - getBrewTime();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper previewPotion(ItemStackHelper potion, ItemStackHelper ingredient) {
        return new ItemStackHelper(BrewingRecipeRegistry.craft(ingredient.getRaw(), potion.getRaw()));
    }

    /**
     * @since 1.9.0
     */
    public List<ItemStackHelper> previewPotions() {
        ItemStack ingredient = getIngredient().getRaw();
        return getPotions().stream().map(stack -> new ItemStackHelper(BrewingRecipeRegistry.craft(ingredient, stack.getRaw()))).toList();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getIngredient() {
        return getSlot(3);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getFuel() {
        return getSlot(4);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<ItemStackHelper> getPotions() {
        return List.of(getSlot(0), getSlot(1), getSlot(2));
    }

}
