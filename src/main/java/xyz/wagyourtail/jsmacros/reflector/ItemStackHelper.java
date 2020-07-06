package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemStackHelper {
    private ItemStack i;
    
    public ItemStackHelper(ItemStack i) {
        this.i = i;
    }
    
    public int getDamage() {
        return i.getDamage();
    }
    
    public int getMaxDamage() {
        return i.getMaxDamage();
    }
    
    public String getName() {
        return i.getName().toString();
    }
    
    public int getCount() {
        return i.getCount();
    }
    
    public int getMaxCount() {
        return i.getMaxCount();
    }
    
    public String getCreativeTab() {
        ItemGroup g = i.getItem().getGroup();
        if (g != null)
            return g.getId();
        else
            return null;
    }
    
    public String getItemID() {
        return i.getItem().toString();
    }
    
    public boolean isEmpty() {
        return i.isEmpty();
    }
    
    public ItemStack getRaw() {
        return i;
    }
}
