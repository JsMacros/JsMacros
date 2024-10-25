package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.FishEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FishEntityHelper<T extends FishEntity> extends MobEntityHelper<T> {

    public FishEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if this fish came from a bucket, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFromBucket() {
        return base.isFromBucket();
    }

}
