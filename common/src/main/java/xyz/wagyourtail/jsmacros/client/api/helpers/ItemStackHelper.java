package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class ItemStackHelper extends BaseHelper<ItemStack> {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public ItemStackHelper(ItemStack i) {
        super(i);
    }
    
    /**
     * Sets the item damage value.
     * 
     * You may want to use {@link ItemStackHelper#copy()} first.
     * 
     * @since 1.2.0
     * 
     * @param damage
     * @return
     */
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
        NbtCompound tag = base.getTag();
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
        return MinecraftClient.getInstance().getNetworkHandler().getTagManager().getOrCreateTagGroup(Registry.ITEM_KEY).getTagsFor(base.getItem()).stream().map(Identifier::toString).collect(Collectors.toList());
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
        return base.equals(ish.getRaw());
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
        return ItemStack.areTagsEqual(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.areTagsEqual(base, is);
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
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(base.copy());
    }
}
