package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class GrindStoneInventory extends Inventory<GrindstoneScreen> {

    public GrindStoneInventory(GrindstoneScreen inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getFirstInput() {
        return getSlot(0);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getSecondInput() {
        return getSlot(1);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

    /**
     * @return the minimum amount of xp the grindstone should return. The maximum is always 2x this value.
     *
     * @since 1.9.0
     */
    public int simulateXp() {
        int xp = 0;
        xp += this.getExperience(getFirstInput().getRaw());
        xp += this.getExperience(getSecondInput().getRaw());
        return xp > 0 ? (int)Math.ceil((double)xp / 2.0) : 0;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    private int getExperience(ItemStack stack) {
        return EnchantmentHelper.get(stack).entrySet().stream().filter(e -> !e.getKey().isCursed()).mapToInt(e -> e.getKey().getMinPower(e.getValue())).sum();
    }

}
