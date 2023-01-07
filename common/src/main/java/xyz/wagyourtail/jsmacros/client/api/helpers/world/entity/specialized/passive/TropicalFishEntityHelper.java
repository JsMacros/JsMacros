package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.passive;

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
     * @return the size of this tropical fish's variant.
     *
     * @since 1.8.4
     */
    public String getSize() {
        return base.getShape() == 0 ? "small" : "large";
    }

    /**
     * @return the base color of this tropical fish's pattern.
     *
     * @since 1.8.4
     */
    public int getBaseColor() {
        return TropicalFishEntity.getBaseDyeColor(base.getVariant()).getId();
    }

    /**
     * @return the pattern color of this tropical fish's pattern.
     *
     * @since 1.8.4
     */
     public int getPatternColor() {
         return TropicalFishEntity.getPatternDyeColor(base.getVariant()).getId();
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
