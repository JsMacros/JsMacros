package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityHelper extends EntityHelper {
    
    public PlayerEntityHelper(Entity e) {
        super(e);
    }
    
    public PlayerAbilitiesHelper getAbilities() {
    	return new PlayerAbilitiesHelper(((PlayerEntity)e).abilities);
    }
    
    public PlayerEntity getRaw() {
        return (PlayerEntity) e;
    }
}
