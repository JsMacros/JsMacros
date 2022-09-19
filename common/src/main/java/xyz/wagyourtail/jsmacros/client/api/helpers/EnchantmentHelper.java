package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class EnchantmentHelper extends BaseHelper<Enchantment> {

    private int level;

    public EnchantmentHelper(Enchantment base) {
        super(base);
    }

    public EnchantmentHelper(Enchantment base, int level) {
        super(base);
        this.level = level;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getMaxLevel() {
        return base.getMaxLevel();
    }

    /**
     * @param level
     * @return
     *
     * @since 1.9.0
     */
    public String getLevelName(int level) {
        return base.getName(level).getString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getName() {
        return String.valueOf(net.minecraft.enchantment.EnchantmentHelper.getEnchantmentId(base));
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getRarity() {
        return base.getRarity().name();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isCursed() {
        return base.isCursed();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isTreasure() {
        return base.isTreasure();
    }

    /**
     * @param item
     * @return
     *
     * @since 1.9.0
     */
    public boolean isAcceptableItem(String item) {
        return base.isAcceptableItem(Registry.ITEM.get(new Identifier(item)).getDefaultStack());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getAcceptableItems() {
        return Registry.ITEM.stream().filter(item -> base.type.isAcceptableItem(item)).map(item -> Registry.ITEM.getId(item).toString()).toList();
    }

    /**
     * @param item
     * @return
     *
     * @since 1.9.0
     */
    public boolean isAcceptableItem(ItemStackHelper item) {
        return base.isAcceptableItem(item.getRaw());
    }

    /**
     * @param enchantment
     * @return
     *
     * @since 1.9.0
     */
    public boolean camCombine(String enchantment) {
        return base.canCombine(Registry.ENCHANTMENT.get(new Identifier(enchantment)));
    }

    /**
     * @param enchantment
     * @return
     *
     * @since 1.9.0
     */
    public boolean camCombine(EnchantmentHelper enchantment) {
        return base.canCombine(enchantment.getRaw());
    }

}
