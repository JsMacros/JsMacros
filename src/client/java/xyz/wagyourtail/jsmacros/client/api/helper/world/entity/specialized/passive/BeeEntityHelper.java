package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.BeeEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class BeeEntityHelper extends AnimalEntityHelper<BeeEntity> {

    public BeeEntityHelper(BeeEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the bee has nectar, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasNectar() {
        return base.hasNectar();
    }

    /**
     * @return {@code true} if the bee is angry at a player, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAngry() {
        return base.hasAngerTime();
    }

    /**
     * @return {@code true} if the bee has already stung a player, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasStung() {
        return base.hasStung();
    }

}
