package xyz.wagyourtail.jsmacros.client.api.helpers.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.google.gson.JsonParseException;
import xyz.wagyourtail.jsmacros.client.api.helpers.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class ItemStackHelper extends BaseHelper<ItemStack> {
    private static final Style LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public ItemStackHelper(ItemStack i) {
        super(i);
    }
    
    /**
     * Sets the item damage value.
     * You should use {@link CreativeItemStackHelper#setDamage(int)} instead.
     * You may want to use {@link ItemStackHelper#copy()} first.
     * 
     * @since 1.2.0
     * 
     * @param damage
     * @return
     */
    @Deprecated
    public ItemStackHelper setDamage(int damage) {
        base.setDamage(damage);
        return this;
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isDamageable() {
        return base.isDamageable();
    }

    /**
     * @return {@code true} if this item is unbreakable, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isUnbreakable() {
        return base.getOrCreateNbt().getBoolean("Unbreakable");
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isEnchantable() {
        return base.isEnchantable();
    }

    /**
     * @return {@code true} if the item is enchanted, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isEnchanted() {
        return base.hasEnchantments();
    }

    /**
     * @return a list of all enchantments on this item.
     *
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
     *         enchanted with the specified enchantment.
     *
     * @since 1.8.4
     */
    public EnchantmentHelper getEnchantment(String id) {
        return getEnchantments().stream().filter(enchantmentHelper -> enchantmentHelper.getName().equals(id)).findFirst().orElse(null);
    }

    /**
     * @param enchantment the enchantment to check for
     * @return {@code true} if the specified enchantment can be applied to this item, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeApplied(EnchantmentHelper enchantment) {
        return enchantment.canBeApplied(this);
    }

    /**
     * @param enchantment the enchantment to check for
     * @return {@code true} if the item is enchanted with the specified enchantment of the same
     *         level, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasEnchantment(EnchantmentHelper enchantment) {
        return getEnchantments().stream().anyMatch(enchantment::equals);
    }

    /**
     * @param enchantment the id of the enchantment to check for
     * @return {@code true} if the item is enchanted with the specified enchantment, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasEnchantment(String enchantment) {
        return hasEnchantment(new EnchantmentHelper(enchantment));
    }

    /**
     * @return a list of all enchantments that can be applied to this item.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getPossibleEnchantments() {
        return Registry.ENCHANTMENT.stream().filter(enchantment -> enchantment.isAcceptableItem(base)).map(EnchantmentHelper::new).toList();
    }

    /**
     * @return a list of all enchantments that can be applied to this item through an enchanting table.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getPossibleEnchantmentsFromTable() {
        return Registry.ENCHANTMENT.stream().filter(enchantment -> enchantment.type.isAcceptableItem(base.getItem()) && !enchantment.isCursed() && !enchantment.isTreasure()).map(EnchantmentHelper::new).toList();
    }

    /**
     * The returned list is a copy of the original list and can be modified without affecting the
     * original item. For editing the actual lore see
     * {@link CreativeItemStackHelper#addLore(Object...)}.
     *
     * @return a list of all lines of lore on this item.
     *
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
                                texts.add(new TextHelper(mutableText2));
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
     *
     * @since 1.8.4
     */
    public int getMaxDurability() {
        return base.getMaxDamage();
    }

    /**
     * @return the current durability of this item.
     *
     * @since 1.8.4
     */
    public int getDurability() {
        return base.getMaxDamage() - base.getDamage();
    }

    /**
     * @return the current repair cost of this item.
     *
     * @since 1.8.4
     */
    public int getRepairCost() {
        return base.getRepairCost();
    }
    
    /**
     * @return the damage taken by this item.
     * 
     * @see #getDurability() 
     */
    public int getDamage() {
        return base.getDamage();
    }
    
    /**
     * @return the maximum damage this item can take.
     * 
     * @see #getMaxDurability() 
     */
    public int getMaxDamage() {
        return base.getMaxDamage();
    }

    /**
     * @return the default attack damage of this item.
     *
     * @since 1.8.4
     */
    public double getAttackDamage() {
        double damage = base.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().mapToDouble(EntityAttributeModifier::getValue).sum();
        return damage + mc.player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }
    
    /**
     * @since 1.2.0
     * @return was string before 1.6.5
     */
    public TextHelper getDefaultName() {
        return new TextHelper(base.getItem().getName());
    }
    
    /**
     * @return was string before 1.6.5
     */
    public TextHelper getName() {
        return new TextHelper(base.getName());
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
     * @since 1.1.6, was a {@link String} until 1.5.1
     * @return
     */
    public NBTElementHelper<?> getNBT() {
        NbtCompound tag = base.getNbt();
        if (tag != null) return NBTElementHelper.resolve(tag);
        else return null;
    }
    
    /**
     * @since 1.1.3
     * @return
     */
    public String getCreativeTab() {
        ItemGroup g = base.getItem().getGroup();
        if (g != null)
            return g.getName();
        else
            return null;
    }
    
    /**
     * @return
     */
     @Deprecated
    public String getItemID() {
        return getItemId();
    }

    /**
     * @since 1.6.4
     * @return
     */
    public String getItemId() {
        return Registry.ITEM.getId(base.getItem()).toString();
    }

    /**
     * @since 1.8.2
     * @return
     */
    public List<String> getTags() {
        return base.getRegistryEntry().streamTags().map(t -> t.id().toString()).collect(Collectors.toList());
    }

    /**
     * @since 1.8.2
     * @return
     */
    public boolean isFood() {
        return base.getItem().isFood();
    }

    /**
     * @since 1.8.2
     * @return
     */
    public boolean isTool() {
        return base.getItem() instanceof ToolItem;
    }

    /**
     * @since 1.8.2
     * @return
     */
    public boolean isWearable() {
        return base.getItem() instanceof Wearable;
    }

    /**
     * @since 1.8.2
     * @return
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
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean equals(ItemStackHelper ish) {
        //ItemStack doesn't overwrite the equals method, so we have to do it ourselves
        return equals(ish.base);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean equals(ItemStack is) {
        return base.isItemEqual(is) && ItemStack.areNbtEqual(base, is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqual(ItemStackHelper ish) {
        return base.isItemEqual(ish.getRaw()) && base.getDamage() == ish.getRaw().getDamage();
    } 
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqual(ItemStack is) {
        return base.isItemEqual(is) && base.getDamage() == is.getDamage();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        return base.isItemEqualIgnoreDamage(ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        return base.isItemEqualIgnoreDamage(is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isNBTEqual(ItemStackHelper ish) {
        return ItemStack.areNbtEqual(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.areNbtEqual(base, is);
    }

    /**
     * @since 1.6.5
     * @return
     */
    public boolean isOnCooldown() {
        return MinecraftClient.getInstance().player.getItemCooldownManager().isCoolingDown(base.getItem());
    }

    /**
     * @since 1.6.5
     * @return
     */
    public float getCooldownProgress() {
        return mc.player.getItemCooldownManager().getCooldownProgress(base.getItem(), mc.getTickDelta());
    }

    /**
     * @param block the block to check
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockHelper block) {
        return base.isSuitableFor(block.getDefaultState().getRaw());
    }

    /**
     * @param block the block to check
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockStateHelper block) {
        return base.isSuitableFor(block.getRaw());
    }

    /**
     * {@link CreativeItemStackHelper} is a subclass of {@link AdvancedItemStackHelper} that adds
     * methods for manipulating the item's nbt data.
     *
     * @return a {@link CreativeItemStackHelper} instance for this item.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper getCreative() {
        return new CreativeItemStackHelper(base);
    }

    /**
     * {@link AdvancedItemStackHelper} is a subclass of {@link ItemStackHelper} that adds methods
     * more advanced methods for interacting with the item and its nbt data.
     *
     * @return a {@link AdvancedItemStackHelper} instance for this item.
     *
     * @since 1.8.4
     */
    public AdvancedItemStackHelper getAdvanced() {
        return new AdvancedItemStackHelper(base);
    }
    
    /**
     * @return the item this stack is made of.
     *
     * @since 1.8.4
     */
    public ItemHelper getItem() {
        return new ItemHelper(base.getItem());
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(base.copy());
    }
}
