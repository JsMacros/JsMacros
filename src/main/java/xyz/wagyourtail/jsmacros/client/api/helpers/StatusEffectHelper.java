package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;

/**
 * @author Wagyourtail
 * @since 1.2.4
 */
public class StatusEffectHelper {
    private StatusEffectInstance s;
    
    public StatusEffectHelper(StatusEffectInstance s) {
        this.s = s;
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public String getId() {
        return Registry.STATUS_EFFECT.getId(s.getEffectType()).toString();
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public int getStrength() {
        return s.getAmplifier();
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public int getTime() {
        return s.getDuration();
    }
    
    public StatusEffectInstance getRaw() {
        return s;
    }
}
