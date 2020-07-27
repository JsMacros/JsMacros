package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityHelper extends EntityHelper {
    
    public PlayerEntityHelper(PlayerEntity e) {
        super(e);
    }
    
    public PlayerAbilitiesHelper getAbilities() {
    	return new PlayerAbilitiesHelper(((PlayerEntity)e).abilities);
    }
    
    public ItemStackHelper getMainHand() {
        return new ItemStackHelper(((PlayerEntity) e).inventory.getMainHandStack());
    }
    
    public ItemStackHelper getOffHand() {
        return new ItemStackHelper(((PlayerEntity) e).inventory.offHand.get(0));
    }
    
    public ItemStackHelper getHeadArmor() {
        return new ItemStackHelper(((PlayerEntity) e).inventory.getArmorStack(3));
    }
    
    public ItemStackHelper getChestArmor() {
        return new ItemStackHelper(((PlayerEntity) e).inventory.getArmorStack(2));
    }
    
    public ItemStackHelper getLegArmor() {
        return new ItemStackHelper(((PlayerEntity) e).inventory.getArmorStack(1));
    }
    
    public ItemStackHelper getFootArmor() {
        return new ItemStackHelper(((PlayerEntity) e).inventory.getArmorStack(0));
    }
    
    public PlayerEntity getRaw() {
        return (PlayerEntity) e;
    }
    
    public String toString() {
        return "Player"+super.toString();
    }
}
