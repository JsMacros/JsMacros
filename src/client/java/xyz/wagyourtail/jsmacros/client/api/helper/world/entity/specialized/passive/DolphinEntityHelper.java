package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.DolphinEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class DolphinEntityHelper extends MobEntityHelper<DolphinEntity> {

    public DolphinEntityHelper(DolphinEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the dolphin has a fish in its mouth, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasFish() {
        return base.hasFish();
    }

    /**
     * The position will be 0 0 0 by default.
     *
     * @return the position of the treasure the dolphin is looking for.
     * @since 1.8.4
     */
    public BlockPosHelper getTreasurePos() {
        return new BlockPosHelper(base.treasurePos);
    }

    /**
     * @return the moisture level of the dolphin.
     * @since 1.8.4
     */
    public int getMoistness() {
        return base.getMoistness();
    }

}
