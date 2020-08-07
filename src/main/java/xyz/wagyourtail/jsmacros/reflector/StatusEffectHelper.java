package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;

public class StatusEffectHelper {
    private StatusEffectInstance s;
    
    public StatusEffectHelper(StatusEffectInstance s) {
        this.s = s;
    }
    
    public String getId() {
        return Registry.STATUS_EFFECT.getId(s.getEffectType()).toString();
    }
    
    public int getStrength() {
        return s.getAmplifier();
    }
    
    public int getTime() {
        return s.getDuration();
    }
    
    public StatusEffectInstance getRaw() {
        return s;
    }
}
