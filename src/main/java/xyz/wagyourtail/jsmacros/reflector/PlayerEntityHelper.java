package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityHelper extends EntityHelper {
    
    public PlayerEntityHelper(PlayerEntity e) {
        super(e);
    }
    
    public PlayerAbilitiesHelper getAbilities() {
    	return new PlayerAbilitiesHelper(((PlayerEntity)e).abilities);
    }
    
    public PlayerEntity getRaw() {
        return (PlayerEntity) e;
    }
    
    public String toString() {
        return "Player"+super.toString();
    }
}
