package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.passive;

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

}
