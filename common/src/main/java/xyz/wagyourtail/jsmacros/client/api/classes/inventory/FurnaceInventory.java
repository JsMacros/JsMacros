package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import xyz.wagyourtail.jsmacros.client.access.IAbstractFurnaceScreenHandler;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class FurnaceInventory extends Inventory<AbstractFurnaceScreen<?>> {

    public FurnaceInventory(AbstractFurnaceScreen<?> inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getInput() {
        return getSlot(0);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getFuel() {
        return getSlot(1);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getResult() {
        return getSlot(2);
    }

    /**
     * @param stack
     * @return
     *
     * @since 1.9.0
     */
    public boolean canUseAsFuel(ItemStackHelper stack) {
        return ((IAbstractFurnaceScreenHandler) inventory.getScreenHandler()).jsmacros_isFuel(stack.getRaw());
    }

    /**
     * @param stack
     * @return
     *
     * @since 1.9.0
     */
    public boolean isSmeltable(ItemStackHelper stack) {
        return ((IAbstractFurnaceScreenHandler) inventory.getScreenHandler()).jsmacros_isSmeltable(stack.getRaw());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Object2IntMap<String> getFuelValues() {
        Object2IntMap<String> fuelMap = new Object2IntOpenHashMap<>();
        for (Map.Entry<Item, Integer> entry : FurnaceBlockEntity.createFuelTimeMap().entrySet()) {
            fuelMap.put(Registry.ITEM.getId(entry.getKey()).toString(), entry.getValue().intValue());
        }
        return fuelMap;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getCookProgress() {
        return inventory.getScreenHandler().getCookProgress();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getFuelProgress() {
        return inventory.getScreenHandler().getFuelProgress();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBurning() {
        return inventory.getScreenHandler().isBurning();
    }

}
