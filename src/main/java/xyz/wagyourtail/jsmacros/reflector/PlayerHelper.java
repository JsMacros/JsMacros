package xyz.wagyourtail.jsmacros.reflector;

import java.util.HashMap;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerHelper {
    private PlayerEntity p;
    
    public PlayerHelper(PlayerEntity p) {
        this.p = p;
    }
    
    public HashMap<String, Double> getPos() {
        HashMap<String, Double> r = new HashMap<>();
        Vec3d pos = p.getPos();
        r.put("x", pos.x);
        r.put("y", pos.y);
        r.put("z", pos.z);
        return r;
    }
    
    public String getName() {
        return p.getName().asFormattedString();
    }
    
    public PlayerEntity getRaw() {
        return p;
    }
}
