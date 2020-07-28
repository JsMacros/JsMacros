package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public class ItemStackHelper {
    private ItemStack i;
    
    public ItemStackHelper(ItemStack i) {
        this.i = i;
    }
    
    public ItemStackHelper setDamage(int damage) {
        i.setDamage(damage);
        return this;
    }
    
    public boolean isDamageable() {
        return i.isDamageable();
    }
    
    public boolean isEnchantable() {
        return i.isEnchantable();
    }
    
    public int getDamage() {
        return i.getDamage();
    }
    
    public int getMaxDamage() {
        return i.getMaxDamage();
    }
    
    public String getDefaultName() {
        return i.getItem().getName().getString();
    }
    
    public String getName() {
        return i.getName().getString();
    }
    
    public int getCount() {
        return i.getCount();
    }
    
    public int getMaxCount() {
        return i.getMaxCount();
    }
    
    public String getNBT() {
        CompoundTag tag = i.getTag();
        if (tag != null) return tag.toString();
        else return "";
    }
    
    public String getCreativeTab() {
        ItemGroup g = i.getItem().getGroup();
        if (g != null)
            return g.getId();
        else
            return null;
    }
    
    public String getItemID() {
        return Registry.ITEM.getId(i.getItem()).toString();
    }
    
    public boolean isEmpty() {
        return i.isEmpty();
    }
    
    public ItemStack getRaw() {
        return i;
    }
    
    public String toString() {
        return String.format("ItemStack:{\"id\":\"%s\", \"damage\": %d, \"count\": %d}", this.getItemID(), i.getDamage(), i.getCount());
    }
    
    public boolean equals(ItemStackHelper ish) {
        return i.equals(ish.getRaw());
    }
    
    public boolean equals(ItemStack is) {
        return i.equals(is);
    }
    
    public boolean isItemEqual(ItemStackHelper ish) {
        return i.isItemEqual(ish.getRaw());
    } 
    
    public boolean isItemEqual(ItemStack is) {
        return i.isItemEqual(is);
    }
    
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        return i.isItemEqualIgnoreDamage(ish.getRaw());
    }
    
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        return i.isItemEqualIgnoreDamage(is);
    }
    
    public boolean isNBTEqual(ItemStackHelper ish) {
        return ItemStack.areTagsEqual(i, ish.getRaw());
    }
    
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.areTagsEqual(i, is);
    }
    
    public ItemStackHelper copy() {
        return new ItemStackHelper(i.copy());
    }
}
