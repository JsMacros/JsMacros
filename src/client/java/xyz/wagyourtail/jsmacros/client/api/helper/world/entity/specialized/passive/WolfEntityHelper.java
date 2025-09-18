package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.WolfEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.DyeColorHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class WolfEntityHelper extends TameableEntityHelper<WolfEntity> {

    public WolfEntityHelper(WolfEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this wolf is tamed and the player has either a bone or meat in one of
     * their hands, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBegging() {
        return base.isBegging();
    }

    /**
     * @return the color of this wolf's collar.
     * @since 1.8.4
     */
    public DyeColorHelper getCollarColor() {
        return new DyeColorHelper(base.getCollarColor());
    }

    /**
     * @return {@code true} if this wolf is angry, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAngry() {
        return base.hasAngerTime();
    }

    /**
     * @return {@code true} if this wolf is wet, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isWet() {
        return base.furWet;
    }

}
