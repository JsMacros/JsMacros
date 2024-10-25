package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;

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
     * @since 1.8.4
     */
    public ItemStackHelper getTopInput() {
        return getSlot(0);
    }

    /**
     * @return the bottom item to disenchant.
     * @since 1.8.4
     */
    public ItemStackHelper getBottomInput() {
        return getSlot(1);
    }

    /**
     * @return the expected output item.
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
     * @since 1.8.4
     */
    public int simulateXp() {
        int xp = 0;
        xp += this.getExperience(getTopInput().getRaw());
        xp += this.getExperience(getBottomInput().getRaw());
        return xp > 0 ? (int) Math.ceil((double) xp / 2.0) : 0;
    }

    private int getExperience(ItemStack stack) {
        int i = 0;
        ItemEnchantmentsComponent lv = EnchantmentHelper.getEnchantments(stack);

        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : lv.getEnchantmentEntries()) {
            RegistryEntry<Enchantment> lv2 = entry.getKey();
            int j = entry.getIntValue();
            if (!lv2.isIn(EnchantmentTags.CURSE)) {
                i += lv2.value().getMinPower(j);
            }
        }

        return i;
    }

    @Override
    public String toString() {
        return String.format("GrindStoneInventory:{}");
    }

}
