package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.SheepEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.DyeColorHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SheepEntityHelper extends AnimalEntityHelper<SheepEntity> {

    public SheepEntityHelper(SheepEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this sheep is sheared, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSheared() {
        return base.isSheared();
    }

    /**
     * @return {@code true} if this sheep can be sheared, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isShearable() {
        return base.isShearable();
    }

    /**
     * @return the color of this sheep.
     * @since 1.8.4
     */
    public DyeColorHelper getColor() {
        return new DyeColorHelper(base.getColor());
    }

    /**
     * Sheep named {@code jeb_} will cycle through all colors when rendered. If sheared, they will
     * drop their original colored wool.
     *
     * @return {@code true} if the sheep has a rainbow overlay, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isJeb() {
        return base.hasCustomName() && "jeb_".equals(base.getName().getString());
    }

}
