package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.passive;

import net.minecraft.entity.passive.AxolotlEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AxolotlEntityHelper extends AnimalEntityHelper<AxolotlEntity> {

    public AxolotlEntityHelper(AxolotlEntity base) {
        super(base);
    }

    /**
     * @return the id of this axolotl's variant.
     * @since 1.8.4
     */
    public int getVariantId() {
        return base.getVariant().getId();
    }

    /**
     * @return the name of this axolotl's variant.
     * @since 1.8.4
     */
    public String getVariantName() {
        return base.getVariant().getName();
    }

    /**
     * @return {@code true} if the axolotl is playing dead, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPlayingDead() {
        return base.isPlayingDead();
    }

    /**
     * @return {@code true} if the axolotl came from a bucket, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFromBucket() {
        return base.isFromBucket();
    }

}
