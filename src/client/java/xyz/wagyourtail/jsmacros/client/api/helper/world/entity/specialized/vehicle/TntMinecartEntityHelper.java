package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.vehicle;

import net.minecraft.entity.vehicle.TntMinecartEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class TntMinecartEntityHelper extends EntityHelper<TntMinecartEntity> {

    public TntMinecartEntityHelper(TntMinecartEntity base) {
        super(base);
    }

    /**
     * @return the remaining time in ticks before the tnt explodes, or {@code -1} if the tnt is not
     * primed.
     * @since 1.8.4
     */
    public int getRemainingTime() {
        return base.getFuseTicks();
    }

    /**
     * @return {@code true} if the tnt is primed, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPrimed() {
        return base.isPrimed();
    }

}
