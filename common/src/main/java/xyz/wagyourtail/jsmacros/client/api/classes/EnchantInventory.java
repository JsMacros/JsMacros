package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.screen.ingame.EnchantingScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.EnchantingScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

/**
 * @since 1.3.1
 */
public class EnchantInventory extends Inventory<EnchantingScreen> {
    
    protected EnchantInventory(EnchantingScreen inventory) {
        super(inventory);
    }
    
    /**
     * @return xp level required to do enchantments
     * @since 1.3.1
     */
    public int[] getRequiredLevels() {
        return ((EnchantingScreenHandler)inventory.screenHandler).enchantmentLevel;
    }
    
    /**
     * @return list of enchantments text.
     * @since 1.3.1
     */
    public TextHelper[] getEnchantments() {
        TextHelper[] enchants = new TextHelper[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.byIndex(((EnchantingScreenHandler)inventory.screenHandler).enchantmentId[j] & 255);
            if (((EnchantingScreenHandler)inventory.screenHandler).enchantmentId[j] > 0 && (enchantment) != null) {
                enchants[j] = new TextHelper(new LiteralText(enchantment.getTranslatedName((((EnchantingScreenHandler)inventory.screenHandler).enchantmentId[j] & 65280) >> 8)));
            }
        }
        return enchants;
    }

    private static String getEnchantId(Enchantment enchantment) {
        for (Identifier id : Enchantment.REGISTRY.getKeySet()) {
            if (Enchantment.getByName(id.toString()) == enchantment) {
                return id.toString();
            }
        }
        return null;
    }
    
    /**
     * @return id for enchantments
     * @since 1.3.1
     */
    public String[] getEnchantmentIds() {
        String[] enchants = new String[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment =  Enchantment.byIndex(((EnchantingScreenHandler)inventory.screenHandler).enchantmentId[j] & 255);
            if (((EnchantingScreenHandler)inventory.screenHandler).enchantmentId[j] >= 0 && (enchantment) != null) {
                enchants[j] = getEnchantId(enchantment);
            }
        }
        return enchants;
    }
    
    /**
     * @return level of enchantments
     * @since 1.3.1
     */
    public int[] getEnchantmentLevels() {
        int[] list = new int[3];
        for (int i = 0; i < 3; ++i) {
            list[i] = (((EnchantingScreenHandler)inventory.screenHandler).enchantmentId[i] & 65280) >> 8;
        }
        return list;
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
        if (inventory.screenHandler.onButtonClick(mc.player, index)) {
            mc.interactionManager.clickButton(inventory.screenHandler.syncId, index);
            return true;
        }
        return false;
    }
    
}
