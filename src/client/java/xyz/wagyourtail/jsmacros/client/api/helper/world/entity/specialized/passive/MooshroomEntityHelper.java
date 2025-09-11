package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.MooshroomEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class MooshroomEntityHelper extends AnimalEntityHelper<MooshroomEntity> {

    public MooshroomEntityHelper(MooshroomEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this mooshroom can be sheared, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isShearable() {
        return base.isShearable();
    }

    /**
     * @return {@code true} if this mooshroom is a red mooshroom, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isRed() {
        return base.getVariant() == MooshroomEntity.Variant.RED;
    }

    /**
     * @return {@code true} if this mooshroom is a brown mooshroom, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBrown() {
        return base.getVariant() == MooshroomEntity.Variant.BROWN;
    }

}
