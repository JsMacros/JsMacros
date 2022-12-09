package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.entity.passive.TropicalFishEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class TropicalFishEntityHelper extends FishEntityHelper<TropicalFishEntity> {

    public TropicalFishEntityHelper(TropicalFishEntity base) {
        super(base);
    }

    /**
     * @return the variant of this tropical fish.
     *
     * @since 1.8.4
     */
    public int getVariant() {
        return base.getVariant();
    }

    /**
     * @return the shape of this tropical fish's variant.
     *
     * @since 1.8.4
     */
    public int getShape() {
        return base.getShape();
    }

    /**
     * @return the pattern of this tropical fish's variant.
     *
     * @since 1.8.4
     */
    public int getPattern() {
        return Math.min((getVariant() & 0xFF00) >> 8, 5);
    }

    /**
     * @return the id of this tropical fish's variant.
     *
     * @since 1.8.4
     */
    public String getVarietyId() {
        return base.getVarietyId().toString();
    }

}
