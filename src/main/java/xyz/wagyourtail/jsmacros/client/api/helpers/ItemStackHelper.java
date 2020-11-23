package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

/**
 * @author Wagyourtail
 *
 */
public class ItemStackHelper {
    private ItemStack i;
    
    public ItemStackHelper(ItemStack i) {
        this.i = i;
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
        i.setDamage(damage);
        return this;
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isDamageable() {
        return i.isDamageable();
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isEnchantable() {
        return i.isEnchantable();
    }
    
    /**
     * @return
     */
    public int getDamage() {
        return i.getDamage();
    }
    
    /**
     * @return
     */
    public int getMaxDamage() {
        return i.getMaxDamage();
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public String getDefaultName() {
        return i.getItem().getName().getString();
    }
    
    /**
     * @return
     */
    public String getName() {
        return i.getName().getString();
    }
    
    /**
     * @return
     */
    public int getCount() {
        return i.getCount();
    }
    
    /**
     * @return
     */
    public int getMaxCount() {
        return i.getMaxCount();
    }
    
    /**
     * @since 1.1.6
     * @return
     */
    public String getNBT() {
        CompoundTag tag = i.getTag();
        if (tag != null) return tag.toString();
        else return "{}";
    }
    
    /**
     * @since 1.1.3
     * @return
     */
    public String getCreativeTab() {
        ItemGroup g = i.getItem().getGroup();
        if (g != null)
            return g.getName();
        else
            return null;
    }
    
    /**
     * @return
     */
    public String getItemID() {
        return Registry.ITEM.getId(i.getItem()).toString();
    }
    
    /**
     * @return
     */
    public boolean isEmpty() {
        return i.isEmpty();
    }
    
    public ItemStack getRaw() {
        return i;
    }
    
    public String toString() {
        return String.format("ItemStack:{\"id\":\"%s\", \"damage\": %d, \"count\": %d}", this.getItemID(), i.getDamage(), i.getCount());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean equals(ItemStackHelper ish) {
        return i.equals(ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean equals(ItemStack is) {
        return i.equals(is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqual(ItemStackHelper ish) {
        return i.isItemEqual(ish.getRaw()) && i.getDamage() == ish.getRaw().getDamage();
    } 
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqual(ItemStack is) {
        return i.isItemEqual(is) && i.getDamage() == is.getDamage();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        return i.isItemEqualIgnoreDamage(ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        return i.isItemEqualIgnoreDamage(is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isNBTEqual(ItemStackHelper ish) {
        return ItemStack.areTagsEqual(i, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.areTagsEqual(i, is);
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(i.copy());
    }
}
