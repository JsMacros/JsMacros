package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Uuids;
import xyz.wagyourtail.doclet.DocletReplaceReturn;

import java.util.Objects;
import java.util.stream.Stream;

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
     * @since 1.8.4
     */
    @DocletReplaceReturn("ParrotVariant")
    public String getVariant() {
        return base.getVariant().asString();
    }

    /**
     * @return {@code true} if this parrot is sitting, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSitting() {
        return base.isInSittingPose();
    }

    /**
     * @return {@code true} if this parrot is flying, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFlying() {
        return base.isInAir();
    }

    /**
     * @return {@code true} if this parrot is dancing to music, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPartying() {
        return base.isSongPlaying();
    }

    /**
     * @return {@code true} if this parrot is just standing around, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isStanding() {
        return !isPartying() && !isFlying() && !isSitting();
    }

    /**
     * @return {@code true} if this parrot is sitting on any player's shoulder, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isSittingOnShoulder() {
        if (!isSitting()) return false;
        return MinecraftClient.getInstance().world.getPlayers().stream()
            .flatMap(e -> Stream.of(e.getShoulderEntityRight(), e.getShoulderEntityLeft()))
            .filter(Objects::nonNull)
            .flatMap(n -> n.getIntArray("UUID").stream())
            .map(Uuids::toUuid)
            .anyMatch(base.getUuid()::equals);
    }

}
