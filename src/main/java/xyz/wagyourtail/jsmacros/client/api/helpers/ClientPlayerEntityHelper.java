package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends ClientPlayerEntity> extends PlayerEntityHelper<T> {

    public ClientPlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<T> lookAt(double yaw, double pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        base.prevPitch = base.pitch;
        base.prevYaw = base.yaw;
        base.pitch = (float)pitch;
        base.yaw = MathHelper.wrapDegrees((float)yaw);
        if (base.getVehicle() != null) {
            base.getVehicle().onPassengerLookAround(base);
        }
        return this;
    }

    /**
     * look at the specified coordinates.
     *
     * @param x
     * @param y
     * @param z
     * @return
     * @since 1.2.8
     */
    public ClientPlayerEntityHelper<T> lookAt(double x, double y, double z) {
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.getX(), base.getY() + base.getEyeHeight(base.getPose()), base.getZ(), x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return base.getHungerManager().getFoodLevel();
    }


    public String toString() {
        return "Client" + super.toString();
    }
}
