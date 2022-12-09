package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.ParrotEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ParrotEntityHelper extends TameableEntityHelper<ParrotEntity> {

    public ParrotEntityHelper(ParrotEntity base) {
        super(base);
    }

    /**
     * @return the variant of this parrot.
     *
     * @since 1.8.4
     */
    public int getVariant() {
        return base.getVariant();
    }

    /**
     * @return {@code true} if this parrot is sitting, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSitting() {
        return base.isInSittingPose();
    }

    /**
     * @return {@code true} if this parrot is flying, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isFlying() {
        return base.isInAir();
    }

    /**
     * @return {@code true} if this parrot is dancing to music, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isPartying() {
        return base.isSongPlaying();
    }

    /**
     * @return {@code true} if this parrot is just standing around, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isStanding() {
        return !isPartying() && !isFlying() && !isSitting();
    }

    /**
     * @return {@code true} if this parrot is sitting on any player's shoulder, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSittingOnShoulder() {
        return MinecraftClient.getInstance().world.getPlayers().stream().anyMatch(
                p -> p.getShoulderEntityLeft().getUuid("UUID").equals(base.getUuid())
                        || p.getShoulderEntityRight().getUuid("UUID").equals(base.getUuid())
        );
    }

}
