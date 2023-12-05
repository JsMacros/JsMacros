package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
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
     * @since 1.8.4
     */
    public StatusEffectHelper(StatusEffect s) {
        this(s, 0);
    }

    /**
     * @since 1.8.4
     */
    public StatusEffectHelper(StatusEffect s, int t) {
        super(new StatusEffectInstance(s, t));
    }

    /**
     * @return
     * @since 1.2.4
     */
    @DocletReplaceReturn("StatusEffectId")
    public String getId() {
        return Registry.STATUS_EFFECT.getId(base.getEffectType()).toString();
    }

    /**
     * @return
     * @since 1.2.4
     */
    public int getStrength() {
        return base.getAmplifier();
    }

    /**
     * @return the string name of the category of the status effect, "HARMFUL", "NEUTRAL", or "BENEFICIAL".
     * @since 1.8.4
     */
    public String getCategory() {
        return switch (base.getEffectType().getType()) {
            case HARMFUL -> "HARMFUL";
            case NEUTRAL -> "NEUTRAL";
            case BENEFICIAL -> "BENEFICIAL";
        };
    }

    /**
     * @return
     * @since 1.2.4
     */
    public int getTime() {
        return base.getDuration();
    }

    /**
     * @return {@code true} if this effect is applied permanently, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPermanent() {
        return false;
    }

    /**
     * Ambient effects are usually applied through beacons and they make the particles more
     * translucent.
     *
     * @return {@code true} if this effect is an ambient one, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAmbient() {
        return base.isAmbient();
    }

    /**
     * @return {@code true} if this effect has an icon it should render, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasIcon() {
        return base.shouldShowIcon();
    }

    /**
     * @return {@code true} if this effect affects the particle color and gets rendered in game,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isVisible() {
        return base.shouldShowParticles();
    }

    /**
     * An effect which is instant can still have a duration, but only if it's set through a
     * command.
     *
     * @return {@code true} if this effect should be applied instantly, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isInstant() {
        return base.getEffectType().isInstant();
    }

    /**
     * @return {@code true} if this effect is considered beneficial, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBeneficial() {
        return base.getEffectType().getType() == StatusEffectType.BENEFICIAL;
    }

    /**
     * @return {@code true} if this effect is considered neutral, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isNeutral() {
        return base.getEffectType().getType() == StatusEffectType.NEUTRAL;
    }

    /**
     * @return {@code true} if this effect is considered harmful, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isHarmful() {
        return base.getEffectType().getType() == StatusEffectType.HARMFUL;
    }

    @Override
    public String toString() {
        return String.format("StatusEffectHelper:{\"id\": \"%s\", \"strength\": %d, \"time\": %d}", getId(), getStrength(), getTime());
    }

}
