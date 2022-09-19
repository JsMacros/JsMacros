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

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isPermanent() {
        return base.isPermanent();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isAmbient() {
        return base.isAmbient();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean shouldShowIcon() {
        return base.shouldShowIcon();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean shouldShowParticles() {
        return base.shouldShowParticles();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getType() {
        return switch (base.getEffectType().getCategory()) {
            case HARMFUL -> "HARMFUL";
            case NEUTRAL -> "NEUTRAL";
            case BENEFICIAL -> "BENEFICIAL";
        };
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isInstant() {
        return base.getEffectType().isInstant();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBeneficial() {
        return base.getEffectType().isBeneficial();
    }
    
}
