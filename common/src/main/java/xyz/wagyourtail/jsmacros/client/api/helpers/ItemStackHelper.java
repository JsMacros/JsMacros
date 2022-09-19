package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import com.google.gson.JsonParseException;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class ItemStackHelper extends BaseHelper<ItemStack> {
    private static Style LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
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
    @Deprecated(since = "1.9.0")
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
     * @since 1.2.0
     * @return
     */
    public boolean isEnchantable() {
        return base.isEnchantable();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isEnchanted() {
        return base.hasEnchantments();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<EnchantmentHelper> getEnchantments() {
        List<EnchantmentHelper> enchantments = new ArrayList<>();
        net.minecraft.enchantment.EnchantmentHelper.get(base).forEach((enchantment, value) -> {
            enchantments.add(new EnchantmentHelper(enchantment, value));
        });
        return enchantments;
    }

    /**
     * @param id
     * @return
     *
     * @since 1.9.0
     */
    public Optional<EnchantmentHelper> getEnchantment(String id) {
        return getEnchantments().stream().filter(enchantmentHelper -> enchantmentHelper.getName().equals(id)).findFirst();
    }

    /**
     * @param id
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasEnchantment(String id) {
        return getEnchantments().stream().anyMatch(enchantmentHelper -> enchantmentHelper.getName().equals(id));
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<EnchantmentHelper> getPossibleEnchantments() {
        return Registry.ENCHANTMENT.stream().filter(enchantment -> enchantment.isAcceptableItem(base)).map(EnchantmentHelper::new).toList();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<EnchantmentHelper> getPossibleEnchantmentsFromTable() {
        return Registry.ENCHANTMENT.stream().filter(enchantment -> enchantment.type.isAcceptableItem(base.getItem())).map(EnchantmentHelper::new).toList();
    }

    /**
     * @return
     *
     * @since 1.9.0
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
     * @return max durability
     *
     * @since 1.9.0
     */
    public int getMaxDurability() {
        return base.getMaxDamage();
    }

    /**
     * @return durability
     *
     * @since 1.9.0
     */
    public int getDurability() {
        return base.getMaxCount() - base.getDamage();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getRepairCost() {
        return base.getRepairCost();
    }
    
    /**
     * @return
     */
    public int getDamage() {
        return base.getDamage();
    }
    
    /**
     * @return
     */
    public int getMaxDamage() {
        return base.getMaxDamage();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getAttackDamage() {
        if (base.getItem() instanceof SwordItem swordItem) {
            return swordItem.getAttackDamage();
        } else if (base.getItem() instanceof MiningToolItem miningToolItem) {
            return miningToolItem.getAttackDamage();
        } else {
            return 0.5f;
        }
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
     * @return
     */
    public int getCount() {
        return base.getCount();
    }
    
    /**
     * @return
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
    
    public String toString() {
        return String.format("ItemStack:{\"id\":\"%s\", \"damage\": %d, \"count\": %d}", this.getItemId(), base.getDamage(), base.getCount());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean equals(ItemStackHelper ish) {
        //ItemStack doesn't overwrite the equals method, so we have to do it ourselves
        return base.isItemEqual(ish.base) && ItemStack.areNbtEqual(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean equals(ItemStack is) {
        return base.equals(is);
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
     * @return
     *
     * @since 1.9.0
     */
    public boolean isAir() {
        return base.getItem() == Items.AIR;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isSuitableFor(BlockHelper block) {
        return base.isSuitableFor(block.getDefaultState().getRaw());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isSuitableFor(BlockStateHelper block) {
        return base.isSuitableFor(block.getRaw());
    }
    
    /**
     * @return
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper getCreativeHelper() {
        return new CreativeItemStackHelper(base);
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(base.copy());
    }
}
