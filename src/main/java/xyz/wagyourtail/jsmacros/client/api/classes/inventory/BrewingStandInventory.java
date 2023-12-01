package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class BrewingStandInventory extends Inventory<BrewingStandScreen> {

    public BrewingStandInventory(BrewingStandScreen inventory) {
        super(inventory);
    }

    /**
     * @param potion the potion to check
     * @return {@code true} if the given potion is can be brewed, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBrewablePotion(ItemStackHelper potion) {
        Potion p = PotionUtil.getPotion(potion.getRaw());
        if (p != Potions.EMPTY) {
            return BrewingRecipeRegistry.isBrewable(p);
        }
        return false;
    }

    /**
     * @param ingredient the item to check
     * @return {@code true} if the given item is a valid ingredient, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isValidIngredient(ItemStackHelper ingredient) {
        return BrewingRecipeRegistry.isValidIngredient(ingredient.getRaw());
    }

    /**
     * @param potion     the potion to check
     * @param ingredient the ingredient to check
     * @return {@code true} if the given potion and ingredient can be brewed together, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isValidRecipe(ItemStackHelper potion, ItemStackHelper ingredient) {
        return BrewingRecipeRegistry.hasRecipe(potion.getRaw(), ingredient.getRaw());
    }

    /**
     * @return the left fuel.
     * @since 1.8.4
     */
    public int getFuelCount() {
        return inventory.getScreenHandler().getFuel();
    }

    /**
     * The maximum fuel count is a constant with the value 20.
     *
     * @return the maximum fuel.
     * @since 1.8.4
     */
    public int getMaxFuelUses() {
        return BrewingStandBlockEntity.field_31324;
    }

    /**
     * @return {@code true} if the brewing stand can brew any of the held potions with the current
     * ingredient, {@code false} otherwise.
     * @since 1.8.4
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
     * @return the time the potions have been brewing.
     * @since 1.8.4
     */
    public int getBrewTime() {
        return BrewingRecipeRegistry.field_30942 * 20 - inventory.getScreenHandler().getBrewTime();
    }

    /**
     * @return the remaining time the potions have to brew.
     * @since 1.8.4
     */
    public int getRemainingTicks() {
        return inventory.getScreenHandler().getBrewTime();
    }

    /**
     * @param potion     the potion
     * @param ingredient the ingredient
     * @return the resulting potion of the given potion and ingredient if it exists and the potion
     * itself otherwise.
     * @since 1.8.4
     */
    public ItemStackHelper previewPotion(ItemStackHelper potion, ItemStackHelper ingredient) {
        return new ItemStackHelper(BrewingRecipeRegistry.craft(ingredient.getRaw(), potion.getRaw()));
    }

    /**
     * @return a list of all resulting potions of the current input.
     * @since 1.8.4
     */
    public List<ItemStackHelper> previewPotions() {
        ItemStack ingredient = getIngredient().getRaw();
        return getPotions().stream().map(stack -> new ItemStackHelper(BrewingRecipeRegistry.craft(ingredient, stack.getRaw()))).collect(Collectors.toList());
    }

    /**
     * @return the ingredient.
     * @since 1.8.4
     */
    public ItemStackHelper getIngredient() {
        return getSlot(3);
    }

    /**
     * @return the fuel item.
     * @since 1.8.4
     */
    public ItemStackHelper getFuel() {
        return getSlot(4);
    }

    /**
     * @return the first potion.
     * @since 1.8.4
     */
    public ItemStackHelper getFirstPotion() {
        return getSlot(0);
    }

    /**
     * @return the second potion.
     * @since 1.8.4
     */
    public ItemStackHelper getSecondPotion() {
        return getSlot(1);
    }

    /**
     * @return the third potion.
     * @since 1.8.4
     */
    public ItemStackHelper getThirdPotion() {
        return getSlot(2);
    }

    /**
     * @return a list of the potions inside the brewing stand.
     * @since 1.8.4
     */
    public List<ItemStackHelper> getPotions() {
        return Arrays.asList(getSlot(0), getSlot(1), getSlot(2));
    }

    @Override
    public String toString() {
        return String.format("BrewingStandInventory:{}");
    }

}
