package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.4
 */
@SuppressWarnings("unused")
public class StatusEffectHelper extends BaseHelper<StatusEffectInstance> {
    
    public StatusEffectHelper(StatusEffectInstance s) {
        super(s);
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public String getId() {
        return Registry.STATUS_EFFECT.getId(base.getEffectType()).toString();
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public int getStrength() {
        return base.getAmplifier();
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public int getTime() {
        return base.getDuration();
    }
}
