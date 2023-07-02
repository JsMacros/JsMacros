package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

/**
 * @since 1.3.1
 */
public class EnchantInventory extends Inventory<EnchantmentScreen> {
    
    protected EnchantInventory(EnchantmentScreen inventory) {
        super(inventory);
    }
    
    /**
     * @return xp level required to do enchantments
     * @since 1.3.1
     */
    public int[] getRequiredLevels() {
        return inventory.getScreenHandler().enchantmentPower;
    }
    
    /**
     * @return list of enchantments text.
     * @since 1.3.1
     */
    public TextHelper[] getEnchantments() {
        TextHelper[] enchants = new TextHelper[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.byRawId(inventory.getScreenHandler().enchantmentId[j]);
            if ((enchantment) != null) {
                enchants[j] = new TextHelper(enchantment.getName(inventory.getScreenHandler().enchantmentLevel[j]));
            }
        }
        return enchants;
    }
    
    /**
     * @return id for enchantments
     * @since 1.3.1
     */
    public String[] getEnchantmentIds() {
        String[] enchants = new String[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.byRawId(inventory.getScreenHandler().enchantmentId[j]);
            if ((enchantment) != null) {
                enchants[j] = Registry.ENCHANTMENT.getId(enchantment).toString();
            }
        }
        return enchants;
    }
    
    /**
     * @return level of enchantments
     * @since 1.3.1
     */
    public int[] getEnchantmentLevels() {
        return inventory.getScreenHandler().enchantmentLevel;
    }
    
    /**
    *  clicks the button to enchant.
    *
     * @param index
     * @return success
     * @since 1.3.1
     */
    public boolean doEnchant(int index) {
        assert mc.interactionManager != null;
        if (inventory.getScreenHandler().onButtonClick(mc.player, index)) {
            mc.interactionManager.clickButton(syncId, index);
            return true;
        }
        return false;
    }
    
}
