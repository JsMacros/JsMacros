package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class GrindStoneInventory extends Inventory<GrindstoneScreen> {

    public GrindStoneInventory(GrindstoneScreen inventory) {
        super(inventory);
    }

    /**
     * @return the upper item to disenchant.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getTopInput() {
        return getSlot(0);
    }

    /**
     * @return the bottom item to disenchant.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getBottomInput() {
        return getSlot(1);
    }

    /**
     * @return the expected output item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

    /**
     * Returns the minimum amount of xp dropped when disenchanting the input items. To calculate the
     * maximum amount of xp, just multiply the return value by 2.
     *
     * @return the minimum amount of xp the grindstone should return.
     *
     * @since 1.8.4
     */
    public int simulateXp() {
        int xp = 0;
        xp += this.getExperience(getTopInput().getRaw());
        xp += this.getExperience(getBottomInput().getRaw());
        return xp > 0 ? (int) Math.ceil((double) xp / 2.0) : 0;
    }

    private int getExperience(ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(e -> !e.getKey().isCursed()).mapToInt(e -> e.getKey().getMinimumPower(e.getValue())).sum();
    }

    @Override
    public String toString() {
        return String.format("GrindStoneInventory:{}");
    }

}