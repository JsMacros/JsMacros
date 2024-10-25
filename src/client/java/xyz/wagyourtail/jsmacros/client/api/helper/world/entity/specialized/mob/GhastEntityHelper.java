package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.GhastEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class GhastEntityHelper extends MobEntityHelper<GhastEntity> {

    public GhastEntityHelper(GhastEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this ghast is currently about to shoot a fireball, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isShooting() {
        return base.isShooting();
    }

}
