package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.WardenEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class WardenEntityHelper extends MobEntityHelper<WardenEntity> {

    public WardenEntityHelper(WardenEntity base) {
        super(base);
    }

    /**
     * @return this warden's anger towards its active target.
     * @since 1.8.4
     */
    public int getAnger() {
        return base.getAnger();
    }

    /**
     * @return {@code true} if this warden is digging into the ground, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDigging() {
        return base.isInPose(EntityPose.DIGGING);
    }

    /**
     * @return {@code true} if this warden is emerging from the ground, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isEmerging() {
        return base.isInPose(EntityPose.EMERGING);
    }

    /**
     * @return {@code true} if this warden is roaring, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isRoaring() {
        return base.isInPose(EntityPose.ROARING);
    }

    /**
     * @return {@code true} if this warden is sniffing, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSniffing() {
        return base.isInPose(EntityPose.SNIFFING);
    }

    /**
     * @return {@code true} if this warden is charging its sonic boom attack, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isChargingSonicBoom() {
        return base.chargingSonicBoomAnimationState.isRunning();
    }

}
