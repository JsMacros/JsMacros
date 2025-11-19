package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PropertyDelegate;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinAbstractFurnaceScreenHandler;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FurnaceInventory extends RecipeInventory<AbstractFurnaceScreen<?>> {

    public FurnaceInventory(AbstractFurnaceScreen<?> inventory) {
        super(inventory);
    }

    @Override
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(inventory.getScreenHandler().getOutputSlot().getStack());
    }

    /**
     * @param x the x position of the input, will always be 0
     * @param y the y position of the input, will always be 0
     * @return the currently smelting item.
     * @since 1.8.4
     */
    public ItemStackHelper getInput(int x, int y) {
        return getSmeltedItem();
    }

    @Override
    public int getCraftingWidth() {
        return 1;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return 1;
    }

    /**
     * @return the currently smelting item.
     * @since 1.8.4
     */
    public ItemStackHelper getSmeltedItem() {
        return getSlot(0);
    }

    /**
     * @return the fuel item.
     * @since 1.8.4
     */
    public ItemStackHelper getFuel() {
        return getSlot(1);
    }

    /**
     * @param stack the item to check
     * @return {@code true} if the item is a valid fuel, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canUseAsFuel(ItemStackHelper stack) {
        return ((MixinAbstractFurnaceScreenHandler) inventory.getScreenHandler()).invokeIsFuel(stack.getRaw());
    }

    /**
     * @param stack the item to check
     * @return {@code true} if the item can be smelted, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSmeltable(ItemStackHelper stack) {
        return ((MixinAbstractFurnaceScreenHandler) inventory.getScreenHandler()).invokeIsSmeltable(stack.getRaw());
    }

    /**
     * @return a map of all valid fuels and their burn times in ticks.
     * @since 1.8.4
     */
    public Map<String, Integer> getFuelValues() {
        Object2IntMap<String> fuelMap = new Object2IntOpenHashMap<>();
        for (Map.Entry<Item, Integer> entry : mc.world.getFuelRegistry().fuelValues.entrySet()) {
            fuelMap.put(Registries.ITEM.getId(entry.getKey()).toString(), entry.getValue().intValue());
        }
        return fuelMap;
    }

    /**
     * If the returned value equals {@link #getTotalSmeltingTime()} then the item is done smelting.
     *
     * @return the current Smelting progress in ticks.
     * @since 1.8.4
     */
    public int getSmeltingProgress() {
        return getPropertyDelegate().get(2);
    }

    /**
     * @return the total smelting time of a single input item in ticks.
     * @since 1.8.4
     */
    public int getTotalSmeltingTime() {
        return getPropertyDelegate().get(3);
    }

    /**
     * @return the remaining time of the smelting progress in ticks.
     * @since 1.8.4
     */
    public int getRemainingSmeltingTime() {
        return getTotalSmeltingTime() - getSmeltingProgress();
    }

    /**
     * @return the remaining fuel time in ticks.
     * @since 1.8.4
     */
    public int getRemainingFuelTime() {
        return getPropertyDelegate().get(0);
    }

    /**
     * @return the total fuel time of the current fuel item in ticks.
     * @since 1.8.4
     */
    public int getTotalFuelTime() {
        return getPropertyDelegate().get(1);
    }

    private PropertyDelegate getPropertyDelegate() {
        return ((MixinAbstractFurnaceScreenHandler) inventory.getScreenHandler()).getPropertyDelegate();
    }

    /**
     * @return {@code true} if the furnace is currently smelting an item, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBurning() {
        return inventory.getScreenHandler().isBurning();
    }

    @Override
    public String toString() {
        return String.format("FurnaceInventory:{}");
    }

}
