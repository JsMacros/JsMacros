package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.other;

import net.minecraft.entity.AreaEffectCloudEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AreaEffectCloudEntityHelper extends EntityHelper<AreaEffectCloudEntity> {

    public AreaEffectCloudEntityHelper(AreaEffectCloudEntity e) {
        super(e);
    }

    /**
     * @return the radius of this cloud.
     *
     * @since 1.8.4
     */
    public float getRadius() {
        return base.getRadius();
    }

    /**
     * @return the color of this cloud.
     *
     * @since 1.8.4
     */
    public int getColor() {
        return base.getColor();
    }

    /**
     * @return the id of this cloud's particles.
     *
     * @since 1.8.4
     */
    public String getParticleType() {
        return base.getParticleType().asString();
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public boolean isWaiting() {
        return base.isWaiting();
    }

}
