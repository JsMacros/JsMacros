package xyz.wagyourtail.jsmacros.client.api.helpers.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class EnchantmentHelper extends BaseHelper<RegistryEntry<Enchantment>> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final int level;

    public EnchantmentHelper(RegistryEntry<Enchantment> base) {
        this(base, 0);
    }

    public EnchantmentHelper(RegistryEntry<Enchantment> base, int level) {
        super(base);
        this.level = level;
    }

    @DocletReplaceParams("enchantment: CanOmitNamespace<EnchantmentId>")
    public EnchantmentHelper(String enchantment) {
        this(mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Identifier.of(enchantment)).orElseThrow());
    }

    /**
     * @return the level of this enchantment.
     * @since 1.8.4
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the minimum possible level of this enchantment that one can get in vanilla.
     * @since 1.8.4
     */
    public int getMinLevel() {
        return base.value().getMinLevel();
    }

    /**
     * @return the maximum possible level of this enchantment that one can get in vanilla.
     * @since 1.8.4
     */
    public int getMaxLevel() {
        return base.value().getMaxLevel();
    }

    /**
     * @param level the level for the name
     * @return the translated name of this enchantment for the given level.
     * @since 1.8.4
     */
    public String getLevelName(int level) {
        return Enchantment.getName(base, level).getString();
    }

    /**
     * Because roman numerals only support positive integers in the range of 1 to 3999, this method
     * will return the arabic numeral for any given level outside that range.
     *
     * @return the translated name of this enchantment for the given level in roman numerals.
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
     * @since 1.8.4
     */
    public TextHelper getRomanLevelName(int level) {
        MutableText mutableText = base.value().description().copy();
        mutableText.formatted(base.isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.GRAY);
        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(" ").append(getRomanNumeral(level));
        }
        return TextHelper.wrap(mutableText);
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
     * @since 1.8.4
     */
    public String getName() {
        return base.value().description().getString();
    }

    /**
     * @return the id of this enchantment.
     * @since 1.8.4
     */
    @DocletReplaceReturn("EnchantmentId")
    public String getId() {
        return base.getIdAsString();
    }

    /**
     * Only accounts for enchantments of the same target type.
     *
     * @return a list of all enchantments that conflict with this one.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getConflictingEnchantments() {
        return getConflictingEnchantments(false);
    }

    /**
     * @param ignoreType whether to check only enchantments that can be applied to the same target
     *                   type.
     * @return a list of all enchantments that conflict with this one.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getConflictingEnchantments(boolean ignoreType) {
        return mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).streamEntries()
            .filter(e -> !Enchantment.canBeCombined(e, base))
            .map(EnchantmentHelper::new)
            .toList();
    }

    /**
     * Only accounts for enchantments of the same target type.
     *
     * @return a list of all enchantments that can be combined with this one.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getCompatibleEnchantments() {
        return getCompatibleEnchantments(false);
    }

    /**
     * @param ignoreType whether to check only enchantments that can be applied to the same target
     *                   type.
     * @return a list of all enchantments that can be combined with this one.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getCompatibleEnchantments(boolean ignoreType) {
        return mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).streamEntries()
            .filter(e -> Enchantment.canBeCombined(e, base))
            .map(EnchantmentHelper::new)
            .toList();
    }

    /**
     * The weight of an enchantment is bound to its rarity. The higher the weight, the more likely
     * it is to be chosen.
     *
     * @return the relative probability of this enchantment being applied to an enchanted item
     * through the enchanting table or a loot table.
     * @since 1.8.4
     */
    public int getWeight() {
        return base.value().getWeight();
    }

    /**
     * Curses are enchantments that can't be removed from the item they were applied to. They
     * usually only have one possible level and can't be upgraded. When combining items with curses
     * on them, they are transferred like any other enchantment. They can't be obtained through
     * enchantment tables, but rather from loot chests, fishing or trading with villagers.
     *
     * @return {@code true} if this enchantment is a curse, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCursed() {
        return base.isIn(EnchantmentTags.CURSE);
    }

    /**
     * Treasures are enchantments that can't be obtained through enchantment tables, but rather from
     * loot chests, fishing or trading with villagers.
     *
     * @return {@code true} if this enchantment is a treasure, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isTreasure() {
        return base.isIn(EnchantmentTags.TREASURE);
    }

    /**
     * @param item the item to check
     * @return {@code true} if this enchantment can be applied to the given item type, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canBeApplied(ItemHelper item) {
        return base.value().isAcceptableItem(item.getRaw().getDefaultStack());
    }

    /**
     * @param item the item to check
     * @return {@code true} if this enchantment can be applied to the given item, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canBeApplied(ItemStackHelper item) {
        return base.value().isAcceptableItem(item.getRaw()) && item.getRaw().getEnchantments().getEnchantments().stream().allMatch(e -> Enchantment.canBeCombined(e, base));
    }

    /**
     * @return a list of all acceptable item ids for this enchantment.
     * @since 1.8.4
     */
    public List<ItemHelper> getAcceptableItems() {
        return base.value().definition().supportedItems().stream().map(e -> new ItemHelper(e.value())).toList();
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment is compatible with the given enchantment,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    @DocletReplaceParams("enchantment: CanOmitNamespace<EnchantmentId>")
    public boolean isCompatible(String enchantment) {
        return Enchantment.canBeCombined(mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Identifier.of(enchantment)).orElseThrow(), base);
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment is compatible with the given enchantment,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCompatible(EnchantmentHelper enchantment) {
        return Enchantment.canBeCombined(enchantment.getRaw(), base);
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment conflicts with the given enchantment, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    @DocletReplaceParams("enchantment: EnchantmentId")
    public boolean conflictsWith(String enchantment) {
        return !isCompatible(enchantment);
    }

    /**
     * @param enchantment the enchantment to check
     * @return {@code true} if this enchantment conflicts with the given enchantment, {@code false}
     * otherwise.
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
        if (!(o instanceof EnchantmentHelper) || !super.equals(o)) {
            return false;
        }
        EnchantmentHelper that = (EnchantmentHelper) o;
        return level == 0 || that.level == 0 || level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, level);
    }

}
