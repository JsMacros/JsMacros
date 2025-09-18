package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.LivingEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ArmorStandEntityHelper extends LivingEntityHelper<ArmorStandEntity> {

    public ArmorStandEntityHelper(ArmorStandEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the armor stand is visible, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isVisible() {
        return !base.isInvisible();
    }

    /**
     * @return {@code true} if the armor is small, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSmall() {
        return base.isSmall();
    }

    /**
     * @return {@code true} if the armor stand has arms, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasArms() {
        return base.shouldShowArms();
    }

    /**
     * @return {@code true} if the armor stand has a base plate, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasBasePlate() {
        return base.shouldShowBasePlate();
    }

    /**
     * @return {@code true} if the armor stand is a marker, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isMarker() {
        return base.isMarker();
    }

    /**
     * The rotation is in the format of {@code [yaw, pitch, roll]}.
     *
     * @return the head rotation of the armor stand.
     * @since 1.8.4
     */
    public float[] getHeadRotation() {
        return toArray(base.getHeadRotation());
    }

    /**
     * The rotation is in the format of {@code [yaw, pitch, roll]}.
     *
     * @return the body rotation of the armor stand.
     * @since 1.8.4
     */
    public float[] getBodyRotation() {
        return toArray(base.getBodyRotation());
    }

    /**
     * The rotation is in the format of {@code [yaw, pitch, roll]}.
     *
     * @return the left arm rotation of the armor stand.
     * @since 1.8.4
     */
    public float[] getLeftArmRotation() {
        return toArray(base.getLeftArmRotation());
    }

    /**
     * The rotation is in the format of {@code [yaw, pitch, roll]}.
     *
     * @return the right arm rotation of the armor stand.
     * @since 1.8.4
     */
    public float[] getRightArmRotation() {
        return toArray(base.getRightArmRotation());
    }

    /**
     * The rotation is in the format of {@code [yaw, pitch, roll]}.
     *
     * @return the left leg rotation of the armor stand.
     * @since 1.8.4
     */
    public float[] getLeftLegRotation() {
        return toArray(base.getLeftLegRotation());
    }

    /**
     * The rotation is in the format of {@code [yaw, pitch, roll]}.
     *
     * @return the right leg rotation of the armor stand.
     * @since 1.8.4
     */
    public float[] getRightLegRotation() {
        return toArray(base.getRightLegRotation());
    }

    private float[] toArray(EulerAngle angle) {
        return new float[]{angle.yaw(), angle.pitch(), angle.roll()};
    }

}
