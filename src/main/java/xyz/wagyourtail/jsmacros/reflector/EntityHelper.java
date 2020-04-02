package xyz.wagyourtail.jsmacros.reflector;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHelper {
    protected Entity e;
    
    public EntityHelper(Entity e) {
        this.e = e;
    }
    
    public HashMap<String, Double> getPos() {
        HashMap<String, Double> r = new HashMap<>();
        Vec3d pos = e.getPos();
        r.put("x", pos.x);
        r.put("y", pos.y);
        r.put("z", pos.z);
        return r;
    }
    
    public String getName() {
        return e.getName().asFormattedString();
    }
    
    public String getType() {
        return e.getType().toString();
    }
    
    public Entity getRaw() {
        return e;
    }
}