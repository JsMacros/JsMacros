package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

/**
 * @since 1.3.1
 */
public class EnchantInventory extends Inventory<GuiEnchantment> {
    
    protected EnchantInventory(GuiEnchantment inventory) {
        super(inventory);
    }
    
    /**
     * @return xp level required to do enchantments
     * @since 1.3.1
     */
    public int[] getRequiredLevels() {
        return ((ContainerEnchantment)inventory.screenHandler).enchantmentLevel;
    }
    
    /**
     * @return list of enchantments text.
     * @since 1.3.1
     */
    public TextHelper[] getEnchantments() {
        TextHelper[] enchants = new TextHelper[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.byRawId(((ContainerEnchantment)inventory.screenHandler).enchantmentId[j] & 255);
            if (((ContainerEnchantment)inventory.screenHandler).enchantmentId[j] > 0 && (enchantment) != null) {
                enchants[j] = new TextHelper(new ChatComponentText(enchantment.getTranslatedName((((ContainerEnchantment)inventory.screenHandler).enchantmentId[j] & 65280) >> 8)));
            }
        }
        return enchants;
    }

    private static String getEnchantId(Enchantment enchantment) {
        for (ResourceLocation id : Enchantment.getSet()) {
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
            Enchantment enchantment =  Enchantment.byRawId(((ContainerEnchantment)inventory.screenHandler).enchantmentId[j] & 255);
            if (((ContainerEnchantment)inventory.screenHandler).enchantmentId[j] >= 0 && (enchantment) != null) {
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
            list[i] = (((ContainerEnchantment)inventory.screenHandler).enchantmentId[i] & 65280) >> 8;
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
