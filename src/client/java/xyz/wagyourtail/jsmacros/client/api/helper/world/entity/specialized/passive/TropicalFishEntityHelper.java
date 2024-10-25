package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.TropicalFishEntity;
import xyz.wagyourtail.doclet.DocletReplaceReturn;

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
     * @since 1.8.4
     */
    @DocletReplaceReturn("TropicalVariant")
    public String getVariant() {
        return base.getVariant().asString();
    }

    /**
     * @return the size of this tropical fish's variant.
     * @since 1.8.4
     */
    @DocletReplaceReturn("TropicalSize")
    public String getSize() {
        return base.getVariant().getSize().name();
    }

    /**
     * @return the base color of this tropical fish's pattern.
     * @since 1.8.4
     */
    public int getBaseColor() {
        return base.getBaseColorComponents().getId();
    }

    /**
     * @return the pattern color of this tropical fish's pattern.
     * @since 1.8.4
     */
    public int getPatternColor() {
        return base.getPatternColorComponents().getId();
    }

    /**
     * @return the id of this tropical fish's variant.
     * @since 1.8.4
     */
    public int getVarietyId() {
        return base.getVariant().getId();
    }

}
