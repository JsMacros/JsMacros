package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.SpiderEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SpiderEntityHelper extends MobEntityHelper<SpiderEntity> {

    public SpiderEntityHelper(SpiderEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this spider is currently climbing a wall, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isClimbing() {
        return base.isClimbing();
    }

}
