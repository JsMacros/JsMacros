package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.passive;

import net.minecraft.entity.passive.FoxEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinFoxEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FoxEntityHelper extends AnimalEntityHelper<FoxEntity> {

    public FoxEntityHelper(FoxEntity base) {
        super(base);
    }

    /**
     * @return the item in this fox's mouth.
     * @since 1.8.4
     */
    public ItemStackHelper getItemInMouth() {
        return getMainHand();
    }

    /**
     * @return {@code true} if this fox is a snow fox, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSnowFox() {
        return base.getVariant() == FoxEntity.Type.SNOW;
    }

    /**
     * @return {@code true} if this fox is a red fox, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isRedFox() {
        return base.getVariant() == FoxEntity.Type.RED;
    }

    /**
     * @return the owner's UUID, or {@code null} if this fox has no owner.
     * @since 1.8.4
     */
    @Nullable
    public String getOwner() {
        return getTrustedUUIDs().get(0).map(UUID::toString).orElse(null);
    }

    /**
     * @return the second owner's name, or {@code null} if this fox has no owner.
     * @since 1.8.4
     */
    @Nullable
    public String getSecondOwner() {
        return getTrustedUUIDs().get(1).map(UUID::toString).orElse(null);
    }

    private List<Optional<UUID>> getTrustedUUIDs() {
        return ((MixinFoxEntity) base).invokeGetTrustedUuids().stream().map(Optional::ofNullable).collect(Collectors.toList());
    }

    /**
     * @param entity the entity to check
     * @return {@code true} if this fox trusts the given entity, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canTrust(EntityHelper<?> entity) {
        return entity.getUUID().equals(getOwner()) || entity.getUUID().equals(getSecondOwner());
    }

    /**
     * @return {@code true} if this fox is preparing its jump, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasFoundTarget() {
        return base.isRollingHead();
    }

    /**
     * @return {@code true} if this fox is sitting, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSitting() {
        return base.isSitting();
    }

    /**
     * @return {@code true} if this fox is wandering around, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isWandering() {
        return base.isWalking();
    }

    /**
     * @return {@code true} if this fox is sleeping, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSleeping() {
        return base.isSleeping();
    }

    /**
     * @return {@code true} if this fox is defending another fox, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDefending() {
        return ((MixinFoxEntity) base).invokeIsAggressive();
    }

    /**
     * @return {@code true} if this fox is just before its leap, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPouncing() {
        return base.isChasing();
    }

    /**
     * @return {@code true} if this fox is jumping, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isJumping() {
        return base.isJumping();
    }

    /**
     * @return {@code true} if this fox is sneaking in preparation of an attack, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isSneaking() {
        return base.isInSneakingPose();
    }

}
