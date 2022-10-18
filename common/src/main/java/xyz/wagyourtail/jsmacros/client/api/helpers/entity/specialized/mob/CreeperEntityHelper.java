package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob;

import net.minecraft.entity.mob.CreeperEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.MobEntityHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinCreeperEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CreeperEntityHelper extends MobEntityHelper<CreeperEntity> {

    public CreeperEntityHelper(CreeperEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the creeper is charged, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCharged() {
        return base.shouldRenderOverlay();
    }

    /**
     * @return {@code true} if the creeper has been ignited, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isIgnited() {
        return base.isIgnited();
    }

    /**
     * A negative value means the creeper is currently defusing, while a positive value means the
     * creeper is currently charging up.
     *
     * @return the change in fuse every tick.
     *
     * @since 1.8.4
     */
    public int getFuseChange() {
        return base.getFuseSpeed();
    }

    /**
     * @return the time the creeper has been charging up.
     *
     * @since 1.8.4
     */
    public int getFuseTime() {
        return ((MixinCreeperEntity) base).getFuseTime();
    }

    /**
     * @return the maximum time the creeper can be charged for before exploding.
     *
     * @since 1.8.4
     */
    public int getMaxFuseTime() {
        return ((MixinCreeperEntity) base).getMaxFuseTime();
    }

    /**
     * @return the remaining time until the creeper explodes with the current fuse time, or
     *         {@code -1} if the creeper is not about to explode.
     *
     * @since 1.8.4
     */
    public int getRemainingFuseTime() {
        return getFuseChange() < 0 ? -1 : getMaxFuseTime() - getFuseTime();
    }

}
