package xyz.wagyourtail.jsmacros.client.api.helpers.inventory;

import com.google.gson.JsonParseException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class ItemStackHelper extends BaseHelper<ItemStack> {
    private static final Style LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    @DocletReplaceParams("id: ItemId, count: int")
    public ItemStackHelper(String id, int count) {
        super(new ItemStack(Registries.ITEM.get(RegistryHelper.parseIdentifier(id)), count));
    }

    public ItemStackHelper(ItemStack i) {
        super(i);
    }

    /**
     * Sets the item damage value.
     * You should use {@link CreativeItemStackHelper#setDamage(int)} instead.
     * You may want to use {@link ItemStackHelper#copy()} first.
     *
     * @param damage
     * @return self
     * @since 1.2.0
     */
    @Deprecated
    public ItemStackHelper setDamage(int damage) {
        base.setDamage(damage);
        return this;
    }

    /**
     * @return
     * @since 1.2.0
     */
    public boolean isDamageable() {
        return base.isDamageable();
    }

    /**
     * @return {@code true} if this item is unbreakable, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isUnbreakable() {
        return base.getOrCreateNbt().getBoolean("Unbreakable");
    }

    /**
     * @return
     * @since 1.2.0
     */
    public boolean isEnchantable() {
        return base.isEnchantable();
    }

    /**
     * @return {@code true} if the item is enchanted, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isEnchanted() {
        return base.hasEnchantments();
    }

    /**
     * @return a list of all enchantments on this item.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getEnchantments() {
        List<EnchantmentHelper> enchantments = new ArrayList<>();
        net.minecraft.enchantment.EnchantmentHelper.get(base).forEach((enchantment, value) -> {
            enchantments.add(new EnchantmentHelper(enchantment, value));
        });
        return enchantments;
    }

    /**
     * @param id the id of the enchantment to check for
     * @return the enchantment instance, containing the level, or {@code null} if the item is not
     * enchanted with the specified enchantment.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: EnchantmentId")
    @Nullable
    public EnchantmentHelper getEnchantment(String id) {
        return getEnchantments().stream().filter(enchantmentHelper -> enchantmentHelper.getName().equals(id)).findFirst().orElse(null);
    }

    /**
     * @param enchantment the enchantment to check for
     * @return {@code true} if the specified enchantment can be applied to this item, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canBeApplied(EnchantmentHelper enchantment) {
        return enchantment.canBeApplied(this);
    }

    /**
     * @param enchantment the enchantment to check for
     * @return {@code true} if the item is enchanted with the specified enchantment of the same
     * level, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasEnchantment(EnchantmentHelper enchantment) {
        return getEnchantments().stream().anyMatch(enchantment::equals);
    }

    /**
     * @param enchantment the id of the enchantment to check for
     * @return {@code true} if the item is enchanted with the specified enchantment, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: EnchantmentId")
    public boolean hasEnchantment(String enchantment) {
        String toCheck = RegistryHelper.parseNameSpace(enchantment);
        return getEnchantments().stream().anyMatch(e -> e.getId().equals(toCheck));
    }

    /**
     * @return a list of all enchantments that can be applied to this item.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getPossibleEnchantments() {
        return Registries.ENCHANTMENT.stream().filter(enchantment -> enchantment.isAcceptableItem(base)).map(EnchantmentHelper::new).collect(Collectors.toList());
    }

    /**
     * @return a list of all enchantments that can be applied to this item through an enchanting table.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getPossibleEnchantmentsFromTable() {
        return Registries.ENCHANTMENT.stream().filter(enchantment -> enchantment.target.isAcceptableItem(base.getItem()) && !enchantment.isCursed() && !enchantment.isTreasure()).map(EnchantmentHelper::new).collect(Collectors.toList());
    }

    /**
     * The returned list is a copy of the original list and can be modified without affecting the
     * original item. For editing the actual lore see
     * {@link CreativeItemStackHelper#addLore(Object...)}.
     *
     * @return a list of all lines of lore on this item.
     * @since 1.8.4
     */
    public List<TextHelper> getLore() {
        List<TextHelper> texts = new ArrayList<>();
        if (base.hasNbt()) {
            if (base.getNbt().contains("display", 10)) {
                NbtCompound nbtCompound = base.getNbt().getCompound("display");
                if (nbtCompound.getType("Lore") == 9) {
                    NbtList nbtList = nbtCompound.getList("Lore", 8);

                    for (int i = 0; i < nbtList.size(); i++) {
                        String string = nbtList.getString(i);
                        try {
                            MutableText mutableText2 = Text.Serializer.fromJson(string);
                            if (mutableText2 != null) {
                                Texts.setStyleIfAbsent(mutableText2, LORE_STYLE);
                                texts.add(TextHelper.wrap(mutableText2));
                            }
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return texts;
    }

    /**
     * @return the maximum durability of this item.
     * @since 1.8.4
     */
    public int getMaxDurability() {
        return base.getMaxDamage();
    }

    /**
     * @return the current durability of this item.
     * @since 1.8.4
     */
    public int getDurability() {
        return base.getMaxDamage() - base.getDamage();
    }

    /**
     * @return the current repair cost of this item.
     * @since 1.8.4
     */
    public int getRepairCost() {
        return base.getRepairCost();
    }

    /**
     * @return the damage taken by this item.
     * @see #getDurability()
     */
    public int getDamage() {
        return base.getDamage();
    }

    /**
     * @return the maximum damage this item can take.
     * @see #getMaxDurability()
     */
    public int getMaxDamage() {
        return base.getMaxDamage();
    }

    /**
     * @return the default attack damage of this item.
     * @since 1.8.4
     */
    public double getAttackDamage() {
        double damage = base.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().mapToDouble(EntityAttributeModifier::getValue).sum();
        return damage + mc.player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    /**
     * @return was string before 1.6.5
     * @since 1.2.0
     */
    public TextHelper getDefaultName() {
        return TextHelper.wrap(base.getItem().getName());
    }

    /**
     * @return was string before 1.6.5
     */
    public TextHelper getName() {
        return TextHelper.wrap(base.getName());
    }

    /**
     * @return the item count this stack is holding.
     */
    public int getCount() {
        return base.getCount();
    }

    /**
     * @return the maximum amount of items this stack can hold.
     */
    public int getMaxCount() {
        return base.getMaxCount();
    }

    /**
     * @return
     * @since 1.1.6, was a {@link String} until 1.5.1
     */
    @Nullable
    public NBTElementHelper.NBTCompoundHelper getNBT() {
        return NBTElementHelper.wrapCompound(base.getNbt());
    }

    /**
     * @return
     * @since 1.1.3
     */
    public List<TextHelper> getCreativeTab() {
        return ItemGroups.getGroups().parallelStream().filter(group -> !group.isSpecial() && group.getDisplayStacks().parallelStream().anyMatch(e -> ItemStack.areItemsEqual(e, base))).map(ItemGroup::getDisplayName).map(TextHelper::wrap).collect(Collectors.toList());
    }

    /**
     * @return
     */
    @DocletReplaceReturn("ItemId")
    @Deprecated
    public String getItemID() {
        return getItemId();
    }

    /**
     * @return
     * @since 1.6.4
     */
    @DocletReplaceReturn("ItemId")
    public String getItemId() {
        return Registries.ITEM.getId(base.getItem()).toString();
    }

    /**
     * @return
     * @since 1.8.2
     */
    @DocletReplaceReturn("JavaList<ItemTag>")
    public List<String> getTags() {
        return base.getRegistryEntry().streamTags().map(t -> t.id().toString()).collect(Collectors.toList());
    }

    /**
     * @return
     * @since 1.8.2
     */
    public boolean isFood() {
        return base.getItem().isFood();
    }

    /**
     * @return
     * @since 1.8.2
     */
    public boolean isTool() {
        return base.getItem() instanceof ToolItem;
    }

    /**
     * @return
     * @since 1.8.2
     */
    public boolean isWearable() {
        return base.getItem() instanceof Equipment && ((Equipment) base.getItem()).getSlotType().isArmorSlot();
    }

    /**
     * @return
     * @since 1.8.2
     */
    public int getMiningLevel() {
        if (isTool()) {
            return ((ToolItem) base.getItem()).getMaterial().getMiningLevel();
        } else {
            return 0;
        }
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ItemStackHelper:{\"id\": \"%s\", \"damage\": %d, \"count\": %d}", this.getItemId(), base.getDamage(), base.getCount());
    }

    /**
     * @param ish
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean equals(ItemStackHelper ish) {
        // ItemStack doesn't overwrite the equals method, so we have to do it ourselves
        return equals(ish.base);
    }

    /**
     * @param is
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean equals(ItemStack is) {
        return ItemStack.areItemsEqual(base, is) && areNbtEqual(base, is);
    }

    /**
     * @param ish
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean isItemEqual(ItemStackHelper ish) {
        return ItemStack.areItemsEqual(base, ish.getRaw()) && base.getDamage() == ish.getRaw().getDamage();
    }

    /**
     * @param is
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean isItemEqual(ItemStack is) {
        return ItemStack.areItemsEqual(is, base) && base.getDamage() == is.getDamage();
    }

    /**
     * @param ish
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        return ItemStack.areItemsEqual(ish.getRaw(), base);
    }

    /**
     * @param is
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        return ItemStack.areItemsEqual(is, base);
    }

    /**
     * @param ish
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean isNBTEqual(ItemStackHelper ish) {
        return areNbtEqual(base, ish.getRaw());
    }

    /**
     * @param is
     * @return
     * @since 1.1.3 [citation needed]
     */
    public boolean isNBTEqual(ItemStack is) {
        return areNbtEqual(base, is);
    }

    /**
     * @return
     * @since 1.6.5
     */
    public boolean isOnCooldown() {
        return MinecraftClient.getInstance().player.getItemCooldownManager().isCoolingDown(base.getItem());
    }

    /**
     * @return
     * @since 1.6.5
     */
    public float getCooldownProgress() {
        return mc.player.getItemCooldownManager().getCooldownProgress(base.getItem(), mc.getTickDelta());
    }

    /**
     * @param block the block to check
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockHelper block) {
        return base.isSuitableFor(block.getDefaultState().getRaw());
    }

    /**
     * @param block the block to check
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockStateHelper block) {
        return base.isSuitableFor(block.getRaw());
    }

    /**
     * {@link CreativeItemStackHelper} adds methods for manipulating the item's nbt data.
     *
     * @return a {@link CreativeItemStackHelper} instance for this item.
     * @since 1.8.4
     */
    public CreativeItemStackHelper getCreative() {
        return new CreativeItemStackHelper(base);
    }

    /**
     * @return the item this stack is made of.
     * @since 1.8.4
     */
    public ItemHelper getItem() {
        return new ItemHelper(base.getItem());
    }

    /**
     * @return
     * @since 1.2.0
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(base.copy());
    }

    /**
     * This flag only affects players in adventure mode and makes sure only specified blocks can be
     * destroyed by this item.
     *
     * @return {@code true} if the can destroy flag is set, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasDestroyRestrictions() {
        return base.getOrCreateNbt().contains("CanDestroy", 9);
    }

    /**
     * This flag only affects players in adventure mode and makes sure this item can only be placed
     * on specified blocks.
     *
     * @return {@code true} if the can place on flag is set, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasPlaceRestrictions() {
        return base.getOrCreateNbt().contains("CanPlaceOn", 9);
    }

    /**
     * @return a list of all filters set for the can destroy flag.
     * @since 1.8.4
     */
    public List<String> getDestroyRestrictions() {
        if (hasDestroyRestrictions()) {
            return base.getOrCreateNbt().getList("CanDestroy", 8).stream().map(NbtElement::asString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * @return a list of all filters set for the can place on flag.
     * @since 1.8.4
     */
    public List<String> getPlaceRestrictions() {
        if (hasPlaceRestrictions()) {
            return base.getOrCreateNbt().getList("CanPlaceOn", 8).stream().map(NbtElement::asString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * @return {@code true} if enchantments are hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean areEnchantmentsHidden() {
        return isFlagSet(ItemStack.TooltipSection.ENCHANTMENTS);
    }

    /**
     * @return {@code true} if modifiers are hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean areModifiersHidden() {
        return isFlagSet(ItemStack.TooltipSection.MODIFIERS);
    }

    /**
     * @return {@code true} if the unbreakable flag is hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isUnbreakableHidden() {
        return isFlagSet(ItemStack.TooltipSection.UNBREAKABLE);
    }

    /**
     * @return {@code true} if the can destroy flag is hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCanDestroyHidden() {
        return isFlagSet(ItemStack.TooltipSection.CAN_DESTROY);
    }

    /**
     * @return {@code true} if the can place flag is hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCanPlaceHidden() {
        return isFlagSet(ItemStack.TooltipSection.CAN_PLACE);
    }

    /**
     * @return {@code true} if additional attributes are hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean areAdditionalsHidden() {
        return isFlagSet(ItemStack.TooltipSection.ADDITIONAL);
    }

    /**
     * @return {@code true} if dye of colored leather armor is hidden, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDyeHidden() {
        return isFlagSet(ItemStack.TooltipSection.DYE);
    }

    protected boolean isFlagSet(ItemStack.TooltipSection section) {
        NbtCompound nbtCompound = base.getOrCreateNbt();
        return (nbtCompound.getInt("HideFlags") & section.getFlag()) != 0;
    }

    /**
     * The old implementation use used in {@link ItemStack} before 1.20. This only checks for the nbt data, the
     * items themselves are not compared.
     *
     * @param left  the first item stack
     * @param right the second item stack
     * @return {@code true} if the two item stacks have equal nbt data, {@code false} otherwise.
     */
    public static boolean areNbtEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        } else if (left.isEmpty() || right.isEmpty()) {
            return false;
        } else if (left.getNbt() == null && right.getNbt() != null) {
            return false;
        } else {
            return left.getNbt() == null || left.getNbt().equals(right.getNbt());
        }
    }

}
