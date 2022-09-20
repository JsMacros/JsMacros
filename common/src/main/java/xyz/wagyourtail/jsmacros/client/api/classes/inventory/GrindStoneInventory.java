package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class GrindStoneInventory extends Inventory<GrindstoneScreen> {

    public GrindStoneInventory(GrindstoneScreen inventory) {
        super(inventory);
    }

    /**
     * @return the upper item to disenchant.
     *
     * @since 1.9.0
     */
    public ItemStackHelper getFirstInput() {
        return getSlot(0);
    }

    /**
     * @return the bottom item to disenchant.
     *
     * @since 1.9.0
     */
    public ItemStackHelper getSecondInput() {
        return getSlot(1);
    }

    /**
     * @return the expected output item.
     *
     * @since 1.9.0
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

    /**
     * To calculate the maximum amount of xp, just multiply the return value by 2. The average is
     * probably 1.5 times the return value.
     *
     * @return the minimum amount of xp the grindstone should return.
     *
     * @since 1.9.0
     */
    public int simulateXp() {
        int xp = 0;
        xp += this.getExperience(getFirstInput().getRaw());
        xp += this.getExperience(getSecondInput().getRaw());
        return xp > 0 ? (int) Math.ceil((double) xp / 2.0) : 0;
    }

    private int getExperience(ItemStack stack) {
        return EnchantmentHelper.get(stack).entrySet().stream().filter(e -> !e.getKey().isCursed()).mapToInt(e -> e.getKey().getMinPower(e.getValue())).sum();
    }

}
