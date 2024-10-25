package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PiglinEntityHelper extends AbstractPiglinEntityHelper<PiglinEntity> {

    public PiglinEntityHelper(PiglinEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this piglin is doing nothing special, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isWandering() {
        return base.getActivity() == PiglinActivity.DEFAULT;
    }

    /**
     * @return {@code true} if this piglin is dancing to music, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDancing() {
        return base.isDancing();
    }

    /**
     * @return {@code true} if this piglin is admiring an item, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAdmiring() {
        return base.getActivity() == PiglinActivity.ADMIRING_ITEM;
    }

    /**
     * @return {@code true} if this piglin is attacking another entity, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isMeleeAttacking() {
        return base.getActivity() == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON;
    }

    /**
     * @return {@code true} if this piglin is currently charging its crossbow, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isChargingCrossbow() {
        return base.getActivity() == PiglinActivity.CROSSBOW_CHARGE;
    }

    /**
     * @return {@code true} if this piglin has its crossbow fully charged, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasCrossbowReady() {
        return base.getActivity() == PiglinActivity.CROSSBOW_HOLD;
    }

}
