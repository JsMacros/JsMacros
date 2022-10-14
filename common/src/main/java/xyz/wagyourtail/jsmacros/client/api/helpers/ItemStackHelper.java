package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.Objects;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class ItemStackHelper extends BaseHelper<ItemStack> {
    protected static final Minecraft mc = Minecraft.getInstance();
    
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
        if (base == null) return this;
        base.setDamage(damage);
        return this;
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isDamageable() {
        if (base == null) return false;
        return base.isDamageable();
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isEnchantable() {
        if (base == null) return false;
        return base.isEnchantable();
    }
    
    /**
     * @return
     */
    public int getDamage() {
        if (base == null) return 0;
        return base.getDamage();
    }
    
    /**
     * @return
     */
    public int getMaxDamage() {
        if (base == null) return 0;
        return base.getMaxDamage();
    }
    
    /**
     * @since 1.2.0
     * @return was string before 1.6.5
     */
    public TextHelper getDefaultName() {
        if (base == null) return null;
        return new TextHelper(new ChatComponentText(base.getItem().getDisplayName(base)));
    }
    
    /**
     * @return was string before 1.6.5
     */
    public TextHelper getName() {
        if (base == null) return null;
        return new TextHelper(new ChatComponentText(base.getName()));
    }
    
    /**
     * @return
     */
    public int getCount() {
        if (base == null) return 0;
        return base.count;
    }
    
    /**
     * @return
     */
    public int getMaxCount() {
        if (base == null) return 0;
        return base.getMaxCount();
    }

    /**
     * @since 1.1.6, was a {@link String} until 1.5.1
     * @return
     */
    public NBTElementHelper<?> getNBT() {
        if (base == null) return null;
        NBTTagCompound tag = base.getTag();
        if (tag != null) return NBTElementHelper.resolve(tag);
        else return null;
    }
    
    /**
     * @since 1.1.3
     * @return
     */
    public String getCreativeTab() {
        if (base == null) return null;
        CreativeTabs g = base.getItem().getItemGroup();
        if (g != null)
            return g.getTranslationKey();
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
        if (base == null) return null;
        return Item.REGISTRY.getIdentifier(base.getItem()).toString();
    }

    /**
     * @since 1.8.2
     * @return
     */
    public List<String> getTags() {
        return ImmutableList.of();
    }

    /**
     * @since 1.8.2
     * @return
     */
    public boolean isFood() {
        if (base == null) return false;
        return base.getItem() instanceof ItemFood;
    }

    /**
     * @since 1.8.2
     * @return
     */
    public boolean isTool() {
        if (base == null) return false;
        return base.getItem() instanceof ItemTool;
    }

    /**
     * @since 1.8.2
     * @return
     */
    public boolean isWearable() {
        if (base == null) return false;
        return base.getItem() instanceof ItemArmor;
    }

    /**
     * @since 1.8.2
     * @return
     */
    public int getMiningLevel() {
        if (isTool()) {
            return Item.ToolMaterial.valueOf(((ItemTool) base.getItem()).getMaterialAsString()).getMiningLevel();
        } else {
            return 0;
        }
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return base == null;
    }
    
    public String toString() {
        if (base == null) return "ItemStack:{null}";
        return String.format("ItemStack:{\"id\":\"%s\", \"damage\": %d, \"count\": %d}", this.getItemId(), base.getDamage(), base.count);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean equals(ItemStackHelper ish) {
        return Objects.equals(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean equals(ItemStack is) {
        return Objects.equals(base, is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqual(ItemStackHelper ish) {
        if (base == null && ish.base == null) return true;
        if (base == null || ish.base == null) return false;
        return base.equalsIgnoreTags(ish.getRaw()) && base.getDamage() == ish.getRaw().getDamage();
    } 
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqual(ItemStack is) {
        if (base == null && is == null) return true;
        if (base == null || is == null) return false;
        return base.equalsIgnoreTags(is) && base.getDamage() == is.getDamage();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        if (base == null && ish.base == null) return true;
        if (base == null || ish.base == null) return false;
        return this.base.getItem() == ish.base.getItem();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        if (base == null && is == null) return true;
        if (base == null || is == null) return false;
        return base.getItem() == is.getItem();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isNBTEqual(ItemStackHelper ish) {
        return ItemStack.equalsIgnoreDamage(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.equalsIgnoreDamage(base, is);
    }

    /**
     * @since 1.6.5
     * @return
     */
    public boolean isOnCooldown() {
        return false;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public float getCooldownProgress() {
        return 1f;
    }

    /**
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        if (base == null) return new ItemStackHelper(null);
        return new ItemStackHelper(base.copy());
    }
}
