package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.Objects;

/**
 * @author Etheradon
 * @since 1.8.4
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

    public EnchantmentHelper(String enchantment) {
        this(Registry.ENCHANTMENT.get(new Identifier(enchantment)));
    }

    /**
     * @return the level of this enchantment.
     *
     * @since 1.8.4
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the minimum possible level of this enchantment that one can get in vanilla.
     *
     * @since 1.8.4
     */
    public int getMinLevel() {
        return base.getMinLevel();
    }

    /**
     * @return the maximum possible level of this enchantment that one can get in vanilla.
     *
     * @since 1.8.4
     */
    public int getMaxLevel() {
        return base.getMaxLevel();
    }

    /**
     * @param level the level for the name
     * @return the translated name of this enchantment for the given level.
     *
     * @since 1.8.4
     */
    public String getLevelName(int level) {
        return base.getName(level).getString();
    }

    /**
     * Because roman numerals only support positive integers in the range of 1 to 3999, this method
     * will return the arabic numeral for any given level outside that range.
     *
     * @return the translated name of this enchantment for the given level in roman numerals.
     *
     * @since 1.8.4
     */
    public TextHelper getRomanLevelName() {
        return getRomanLevelName(level);
    }

    /**
     * Because roman numerals only support positive integers in the range of 1 to 3999, this method
     * will return the arabic numeral for any given level outside that range.
     *
     * @param level the level for the name
     * @return the translated name of this enchantment for the given level in roman numerals.
     *
     * @since 1.8.4
     */
    public TextHelper getRomanLevelName(int level) {
        MutableText mutableText = Text.translatable(base.getTranslationKey());
        mutableText.formatted(base.isCursed() ? Formatting.RED : Formatting.GRAY);
        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(" ").append(getRomanNumeral(level));
        }
        return new TextHelper(mutableText);
    }

    private static String getRomanNumeral(int number) {
        if (number > 3999 || number < 1) {
            return String.valueOf(number);
        }
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] letters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder romanNumeral = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number = number - values[i];
                romanNumeral.append(letters[i]);
            }
        }
        return romanNumeral.toString();
    }

    /**
     * @return the name of this enchantment.
     *
     * @since 1.8.4
     */
    public String getName() {
        return Text.translatable(base.getTranslationKey()).getString();
    }

    /**
     * @return the id of this enchantment.
     *
     * @since 1.8.4
     */
    public String getId() {
        return Registry.ENCHANTMENT.getId(base).toString();
    }

    /**
     * @return the rarity of this enchantment.
     *
     * @since 1.8.4
     */
    public String getRarity() {
        return switch (base.getRarity()) {
            case COMMON -> "COMMON";
            case UNCOMMON -> "UNCOMMON";
            case RARE -> "RARE";
            case VERY_RARE -> "VERY_RARE";
        };
    }

    /**
     * Only accounts for enchantments of the same target type.
     *
     * @return a list of all enchantments that conflict with this one.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getConflictingEnchantments() {
        return getConflictingEnchantments(false);
    }

    /**
     * @param ignoreType whether to check only enchantments that can be applied to the same target
     *                   type.
     * @return a list of all enchantments that conflict with this one.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getConflictingEnchantments(boolean ignoreType) {
        return Registry.ENCHANTMENT.stream().filter(e -> e != base && (ignoreType || e.type == base.type) && !e.canCombine(base)).map(EnchantmentHelper::new).toList();
    }

    /**
     * Only accounts for enchantments of the same target type.
     *
     * @return a list of all enchantments that can be combined with this one.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getCompatibleEnchantments() {
        return getCompatibleEnchantments(false);
    }

    /**
     * @param ignoreType whether to check only enchantments that can be applied to the same target
     *                   type.
     * @return a list of all enchantments that can be combined with this one.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getCompatibleEnchantments(boolean ignoreType) {
        return Registry.ENCHANTMENT.stream().filter(e -> e != base && (ignoreType || e.type == base.type) && e.canCombine(base)).map(EnchantmentHelper::new).toList();
    }

    /**
     * @return the type of item this enchantment is compatible with.
     *
     * @since 1.8.4
     */
    public String getTargetType() {
        return switch (base.type) {
            case ARMOR -> "ARMOR";
            case ARMOR_FEET -> "ARMOR_FEET";
            case ARMOR_LEGS -> "ARMOR_LEGS";
            case ARMOR_CHEST -> "ARMOR_CHEST";
            case ARMOR_HEAD -> "ARMOR_HEAD";
            case WEAPON -> "WEAPON";
            case DIGGER -> "DIGGER";
            case FISHING_ROD -> "FISHING_ROD";
            case TRIDENT -> "TRIDENT";
            case BREAKABLE -> "BREAKABLE";
            case BOW -> "BOW";
            case WEARABLE -> "WEARABLE";
            case CROSSBOW -> "CROSSBOW";
            case VANISHABLE -> "VANISHABLE";
        };
    }

    /**
     * The weight of an enchantment is bound to its rarity. The higher the weight, the more likely
     * it is to be chosen.
     *
     * @return the relative probability of this enchantment being applied to an enchanted item
     *         through the enchanting table or a loot table.
     *
     * @since 1.8.4
     */
    public int getWeight() {
        return base.getRarity().getWeight();
    }

    /**
     * Curses are enchantments that can't be removed from the item they were applied to. They
     * usually only have one possible level and can't be upgraded. When combining items with curses
     * on them, they are transferred like any other enchantment. They can't be obtained through
     * enchantment tables, but rather from loot chests, fishing or trading with villagers.
     *
     * @return {@code true} if this enchantment is a curse, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCursed() {
        return base.isCursed();
    }

    /**
     * Treasures are enchantments that can't be obtained through enchantment tables, but rather from
     * loot chests, fishing or trading with villagers.
     *
     * @return {@code true} if this enchantment is a treasure, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isTreasure() {
        return base.isTreasure();
    }

    /**
     * @param item the item to check
     * @return {@code true} if this enchantment can be applied to the given item type, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeApplied(ItemHelper item) {
        return base.isAcceptableItem(item.getRaw().getDefaultStack());
    }

    /**
     * @param item the item to check
     * @return {@code true} if this enchantment can be applied to the given item, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeApplied(ItemStackHelper item) {
        return base.isAcceptableItem(item.getRaw()) && item.getEnchantments().stream().allMatch(e -> e.isCompatible(this));
    }

    /**
     * @return a list of all acceptable item ids for this enchantment.
     *
     * @since 1.8.4
     */
    public List<ItemHelper> getAcceptableItems() {
        return Registry.ITEM.stream().filter(item -> base.type.isAcceptableItem(item)).map(ItemHelper::new).toList();
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment is compatible with the given enchantment,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCompatible(String enchantment) {
        return base.canCombine(Registry.ENCHANTMENT.get(RegistryHelper.parseIdentifier(enchantment)));
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment is compatible with the given enchantment,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCompatible(EnchantmentHelper enchantment) {
        return base.canCombine(enchantment.getRaw());
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment conflicts with the given enchantment, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean conflictsWith(String enchantment) {
        return !isCompatible(enchantment);
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment conflicts with the given enchantment, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean conflictsWith(EnchantmentHelper enchantment) {
        return !isCompatible(enchantment);
    }

    @Override
    public String toString() {
        return String.format("EnchantmentHelper:{\"id\": \"%s\", \"level\": %d}", getId(), getLevel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnchantmentHelper that) || !super.equals(o)) {
            return false;
        }
        return level == 0 || that.level == 0 || level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, level);
    }

}
