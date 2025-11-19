package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.PigEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PigEntityHelper extends AnimalEntityHelper<PigEntity> {

    public PigEntityHelper(PigEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this pig is saddled, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSaddled() {
        return base.hasSaddleEquipped();
    }

}
