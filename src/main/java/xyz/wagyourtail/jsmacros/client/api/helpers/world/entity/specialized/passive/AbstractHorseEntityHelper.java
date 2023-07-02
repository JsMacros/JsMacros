package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.passive;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinAbstractHorseEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AbstractHorseEntityHelper<T extends AbstractHorseEntity> extends AnimalEntityHelper<T> {

    public AbstractHorseEntityHelper(T base) {
        super(base);
    }

    /**
     * @return the UUID of this horse's owner, or {@code null} if it has no owner.
     * @since 1.8.4
     */
    public String getOwner() {
        return base.getOwnerUuid() == null ? null : base.getOwnerUuid().toString();
    }

    /**
     * @return {@code true} if this horse is already tamed, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isTame() {
        return base.isTame();
    }

    /**
     * @return {@code true} if this horse is saddled, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSaddled() {
        return base.isSaddled();
    }

    /**
     * @return {@code true} if this horse is angry, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAngry() {
        return base.isAngry();
    }

    /**
     * @return {@code true} if this horse was bred and not naturally spawned, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isBred() {
        return base.isBred();
    }

    /**
     * @return {@code true} if this horse is currently eating, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isEating() {
        return base.isEatingGrass();
    }

    /**
     * @return {@code true} if this horse can wear armor, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canWearArmor() {
        return base.hasArmorSlot();
    }

    /**
     * @return {@code true} if this horse can be saddled, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canBeSaddled() {
        return base.canBeSaddled();
    }

    /**
     * @return this horse's inventory size.
     * @since 1.8.4
     */
    public int getInventorySize() {
        return ((MixinAbstractHorseEntity) base).invokeGetInventorySize();
    }

    /**
     * @return this horse's jump strength.
     * @since 1.8.4
     */
    public double getJumpStrengthStat() {
        return base.getJumpStrength();
    }

    /**
     * The result of this method is only an approximation, but it's really close.
     *
     * @return this horse's maximum jump height for its current jump strength.
     * @since 1.8.4
     */
    public double getHorseJumpHeight() {
        double jumpStrength = base.getJumpStrength();
        return -0.1817584952 * Math.pow(jumpStrength, 3) + 3.689713992 * Math.pow(jumpStrength, 2) + 2.128599134 * jumpStrength - 0.343930367;
    }

    /**
     * @return the maximum possible value of a horse's jump strength.
     * @since 1.8.4
     */
    public int getMaxJumpStrengthStat() {
        return 1;
    }

    /**
     * @return the minimum possible value of a horse's jump strength.
     * @since 1.8.4
     */
    public double getMinJumpStrengthStat() {
        return 0.4;
    }

    /**
     * @return this horse's speed stat.
     * @since 1.8.4
     */
    public double getSpeedStat() {
        return base.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    /**
     * @return this horse's speed in blocks per second.
     * @since 1.8.4
     */
    public double getHorseSpeed() {
        return getSpeedStat() * 42.16;
    }

    /**
     * @return the maximum possible value of a horse's speed stat.
     * @since 1.8.4
     */
    public double getMaxSpeedStat() {
        return 0.3375;
    }

    /**
     * @return the minimum possible value of a horse's speed stat.
     * @since 1.8.4
     */
    public double getMinSpeedStat() {
        return 0.1125;
    }

    /**
     * The returned value is equal to {@link #getMaxHealth()}.
     *
     * @return this horse's health stat.
     * @since 1.8.4
     */
    public double getHealthStat() {
        return base.getMaxHealth();
    }

    /**
     * @return the maximum possible value of a horse's health stat.
     * @since 1.8.4
     */
    public int getMaxHealthStat() {
        return 30;
    }

    /**
     * @return the minimum possible value of a horse's health stat.
     * @since 1.8.4
     */
    public int getMinHealthStat() {
        return 15;
    }

}
