package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.PolarBearEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PolarBearEntityHelper extends AnimalEntityHelper<PolarBearEntity> {

    public PolarBearEntityHelper(PolarBearEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the polar bear is standing up to attack, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAttacking() {
        return base.isWarning();
    }

}
