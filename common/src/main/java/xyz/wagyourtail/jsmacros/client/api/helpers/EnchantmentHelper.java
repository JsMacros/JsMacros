package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EnchantmentHelper extends BaseHelper<Enchantment> {

    private final int level;

    public EnchantmentHelper(Enchantment base) {
        this(base, 0);
    }

    public EnchantmentHelper(Enchantment base, int level) {
        super(base);
        this.level = level;
    }

    /**
     * @return the level of this enchantment.
     *
     * @since 1.9.0
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the maximum level of this enchantment.
     *
     * @since 1.9.0
     */
    public int getMaxLevel() {
        return base.getMaxLevel();
    }

    /**
     * @param level the level for the name.
     * @return the name of this enchantment for the given level.
     *
     * @since 1.9.0
     */
    public String getLevelName(int level) {
        return base.getName(level).getString();
    }

    /**
     * @return the name of this enchantment.
     *
     * @since 1.9.0
     */
    public String getName() {
        return Text.translatable(base.getTranslationKey()).getString();
    }

    /**
     * @return the id of this enchantment.
     *
     * @since 1.9.0
     */
    public String getId() {
        return Registry.ENCHANTMENT.getId(base).toString();
    }

    /**
     * @return the rarity of this enchantment.
     *
     * @since 1.9.0
     */
    public String getRarity() {
        return base.getRarity().name();
    }

    /**
     * @return {@code true} if this enchantment is a curse, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isCursed() {
        return base.isCursed();
    }

    /**
     * @return {@code true} if this enchantment is a treasure, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isTreasure() {
        return base.isTreasure();
    }

    /**
     * @param item the item to check.
     * @return {@code true} if this enchantment can be applied to the given item, {@code false}
     *         otherwise.
     *
     * @since 1.9.0
     */
    public boolean isAcceptableItem(ItemHelper item) {
        return base.isAcceptableItem(item.getRaw().getDefaultStack());
    }

    /**
     * @param item the item to check.
     * @return {@code true} if this enchantment can be applied to the given item, {@code false}
     *         otherwise.
     *
     * @since 1.9.0
     */
    public boolean isAcceptableItem(ItemStackHelper item) {
        return base.isAcceptableItem(item.getRaw());
    }

    /**
     * @return a list of all acceptable item ids for this enchantment.
     *
     * @since 1.9.0
     */
    public List<String> getAcceptableItems() {
        return Registry.ITEM.stream().filter(item -> base.type.isAcceptableItem(item)).map(item -> Registry.ITEM.getId(item).toString()).toList();
    }

    /**
     * @param enchantment the enchantment to check.
     * @return {@code true} if this enchantment is compatible with the given enchantment,
     *         {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean camCombine(String enchantment) {
        return base.canCombine(Registry.ENCHANTMENT.get(new Identifier(enchantment)));
    }

    /**
     * @param enchantment the enchantment to check.
     * @return {@code true} if this enchantment is compatible with the given enchantment,
     *         {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean camCombine(EnchantmentHelper enchantment) {
        return base.canCombine(enchantment.getRaw());
    }

}
