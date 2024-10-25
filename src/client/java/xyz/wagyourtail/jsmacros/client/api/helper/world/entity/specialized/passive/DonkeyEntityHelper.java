package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.AbstractDonkeyEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class DonkeyEntityHelper<T extends AbstractDonkeyEntity> extends AbstractHorseEntityHelper<T> {

    public DonkeyEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if the donkey is carrying a chest, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasChest() {
        return base.hasChest();
    }

}
