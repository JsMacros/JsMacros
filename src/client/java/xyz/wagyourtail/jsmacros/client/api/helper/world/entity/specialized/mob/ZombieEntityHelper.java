package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.ZombieEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ZombieEntityHelper<T extends ZombieEntity> extends MobEntityHelper<T> {

    public ZombieEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if this zombie is converting to a drowned, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isConvertingToDrowned() {
        return base.isConvertingInWater();
    }

}
