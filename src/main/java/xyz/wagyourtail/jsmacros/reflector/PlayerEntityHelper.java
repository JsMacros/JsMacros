package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class PlayerEntityHelper extends EntityHelper {
    
    public PlayerEntityHelper(PlayerEntity e) {
        super(e);
    }
    
    public PlayerAbilitiesHelper getAbilities() {
    	return new PlayerAbilitiesHelper(((PlayerEntity)e).abilities);
    }
    
    public ItemStackHelper getMainHand() {
        PlayerInventory i = ((PlayerEntity) e).inventory;
        if (i == null) return null;
        return new ItemStackHelper(i.getMainHandStack());
    }
    
    public ItemStackHelper getOffHand() {
        PlayerInventory i = ((PlayerEntity) e).inventory;
        if (i == null) return null;
        return new ItemStackHelper(i.offHand.get(0));
    }
    
    public ItemStackHelper getHeadArmor() {
        PlayerInventory i = ((PlayerEntity) e).inventory;
        if (i == null) return null;
        return new ItemStackHelper(i.getArmorStack(3));
    }
    
    public ItemStackHelper getChestArmor() {
        PlayerInventory i = ((PlayerEntity) e).inventory;
        if (i == null) return null;
        return new ItemStackHelper(i.getArmorStack(2));
    }
    
    public ItemStackHelper getLegArmor() {
        PlayerInventory i = ((PlayerEntity) e).inventory;
        if (i == null) return null;
        return new ItemStackHelper(i.getArmorStack(1));
    }
    
    public ItemStackHelper getFootArmor() {
        PlayerInventory i = ((PlayerEntity) e).inventory;
        if (i == null) return null;
        return new ItemStackHelper(i.getArmorStack(0));
    }
    
    public int getXP() {
        return ((PlayerEntity)e).experienceLevel;
    }
    
    public PlayerEntity getRaw() {
        return (PlayerEntity) e;
    }
    
    public String toString() {
        return "Player"+super.toString();
    }
}
