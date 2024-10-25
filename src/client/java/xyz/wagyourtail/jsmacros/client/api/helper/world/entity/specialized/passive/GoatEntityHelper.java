package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.GoatEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class GoatEntityHelper extends AnimalEntityHelper<GoatEntity> {

    public GoatEntityHelper(GoatEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this goat is currently screaming, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isScreaming() {
        return base.isScreaming();
    }

    /**
     * @return {@code true} if this goat has its left horn still left, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasLeftHorn() {
        return base.hasLeftHorn();
    }

    /**
     * @return {@code true} if this goat has its right horn still left, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasRightHorn() {
        return base.hasRightHorn();
    }

    /**
     * @return {@code true} if this goat still has a horn, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasHorns() {
        return hasLeftHorn() || hasRightHorn();
    }

}
