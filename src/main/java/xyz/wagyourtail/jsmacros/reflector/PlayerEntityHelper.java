package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityHelper extends EntityHelper {
    
    public PlayerEntityHelper(Entity e) {
        super(e);
    }

    public PlayerEntity getRaw() {
        return (PlayerEntity) e;
    }
}
