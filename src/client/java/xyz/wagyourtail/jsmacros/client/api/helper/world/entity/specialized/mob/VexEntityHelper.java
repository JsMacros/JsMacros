package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.VexEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class VexEntityHelper extends MobEntityHelper<VexEntity> {

    public VexEntityHelper(VexEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this vex is currently charging at its target, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isCharging() {
        return base.isCharging();
    }

}
