package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.SnowGolemEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SnowGolemEntityHelper extends MobEntityHelper<SnowGolemEntity> {

    public SnowGolemEntityHelper(SnowGolemEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the snow golem has a pumpkin on its head, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasPumpkin() {
        return base.hasPumpkin();
    }

    /**
     * @return {@code true} if this snow golem can be sheared, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isShearable() {
        return base.isShearable();
    }

}
