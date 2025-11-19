package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class DirectionHelper extends BaseHelper<Direction> {

    public DirectionHelper(Direction base) {
        super(base);
    }

    /**
     * @return the name of this direction.
     * @since 1.8.4
     */
    public String getName() {
        return base.getId();
    }

    /**
     * @return the name of the axis this direction is aligned to.
     * @since 1.8.4
     */
    public String getAxis() {
        return base.getAxis().getId();
    }

    /**
     * @return {@code true} if this direction is vertical, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isVertical() {
        return base.getAxis().isVertical();
    }

    /**
     * @return {@code true} if this direction is horizontal, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isHorizontal() {
        return base.getAxis().isHorizontal();
    }

    /**
     * @return {@code true} if this direction is pointing in a positive direction, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isTowardsPositive() {
        return base.getDirection().offset() == 1;
    }

    /**
     * @return the yaw of this direction.
     * @since 1.8.4
     */
    public float getYaw() {
        return base.getPositiveHorizontalDegrees();
    }

    /**
     * @return the pitch of this direction.
     * @since 1.8.4
     */
    public float getPitch() {
        if (isHorizontal()) {
            return 0;
        } else {
            return base.getOffsetY() * 90;
        }
    }

    /**
     * @return the opposite direction.
     * @since 1.8.4
     */
    public DirectionHelper getOpposite() {
        return new DirectionHelper(base.getOpposite());
    }

    /**
     * @return the direction to the left.
     * @since 1.8.4
     */
    public DirectionHelper getLeft() {
        return new DirectionHelper(base.rotateYCounterclockwise());
    }

    /**
     * @return the direction to the right.
     * @since 1.8.4
     */
    public DirectionHelper getRight() {
        return new DirectionHelper(base.rotateYClockwise());
    }

    /**
     * @return the direction as a directional vector.
     * @since 1.8.4
     */
    public Pos3D getVector() {
        Vec3i vec = base.getVector();
        return new Pos3D(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * @param yaw the yaw to check
     * @return {@code true} if the yaw is facing this direction more than any other one,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean pointsTo(double yaw) {
        return base.pointsTo((float) yaw);
    }

    @Override
    public String toString() {
        return String.format("DirectionHelper:{\"name\": \"%s\", \"yaw\": %f, \"pitch\": %f}", getName(), getYaw(), getPitch());
    }

}
