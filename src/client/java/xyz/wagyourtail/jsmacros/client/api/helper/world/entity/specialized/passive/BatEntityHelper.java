package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.BatEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class BatEntityHelper extends MobEntityHelper<BatEntity> {

    public BatEntityHelper(BatEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the bat is hanging upside down, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isResting() {
        return base.isRoosting();
    }

}
