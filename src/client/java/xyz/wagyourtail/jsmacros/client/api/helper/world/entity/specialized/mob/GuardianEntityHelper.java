package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class GuardianEntityHelper extends MobEntityHelper<GuardianEntity> {

    public GuardianEntityHelper(GuardianEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this guardian is an elder guardian, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isElder() {
        return base instanceof ElderGuardianEntity;
    }

    /**
     * @return {@code true} if this guardian is targeting a mob, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasTarget() {
        return base.hasBeamTarget();
    }

    /**
     * @return the target of this guardian's beam, or {@code null} if it has no target.
     * @since 1.8.4
     */
    @Nullable
    public EntityHelper<?> getTarget() {
        return hasTarget() ? EntityHelper.create(base.getBeamTarget()) : null;
    }

    /**
     * @return {@code true} if this guardian has its spikes extended, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasSpikesRetracted() {
        return !base.areSpikesRetracted();
    }

}
