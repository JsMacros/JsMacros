package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class DirectionHelper extends BaseHelper<Direction> {

    public DirectionHelper(Direction base) {
        super(base);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getName() {
        return base.getName();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getAxis() {
        return base.getAxis().toString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isVertical() {
        return base.getAxis().isVertical();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isHorizontal() {
        return base.getAxis().isHorizontal();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isTowardsPositive() {
        return base.getDirection().offset() == 1;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getYaw() {
        if (base.getHorizontal() == -1) {
            return 0;
        } else {
            return base.asRotation();
        }
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getPitch() {
        if (isHorizontal()) {
            return 0;
        } else {
            return base.getOffsetY() * 90;
        }
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public DirectionHelper getOpposite() {
        return new DirectionHelper(base.getOpposite());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public DirectionHelper getLeft() {
        return new DirectionHelper(base.rotateYCounterclockwise());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public DirectionHelper getRight() {
        return new DirectionHelper(base.rotateYClockwise());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public PositionCommon.Pos3D getVector() {
        Vec3i vec = base.getVector();
        return new PositionCommon.Pos3D(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * @param yaw
     * @return
     *
     * @since 1.9.0
     */
    public boolean pointsTo(float yaw) {
        return base.pointsTo(yaw);
    }

}
