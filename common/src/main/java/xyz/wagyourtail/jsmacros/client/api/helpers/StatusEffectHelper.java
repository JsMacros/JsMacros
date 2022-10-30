package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
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
        return StatusEffect.field_3164.getIdentifier(base.getStatusEffect()).toString();
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
