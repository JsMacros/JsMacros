package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
public class ClientPlayerEntityHelper extends PlayerEntityHelper {

    public ClientPlayerEntityHelper(ClientPlayerEntity e) {
        super(e);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper lookAt(float yaw, float pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        e.prevPitch = e.pitch;
        e.prevYaw = e.yaw;
        e.pitch = pitch;
        e.yaw = MathHelper.fwrapDegrees(yaw);
        if (e.getVehicle() != null) {
            e.getVehicle().onPassengerLookAround(e);
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
    public ClientPlayerEntityHelper lookAt(double x, double y, double z) {
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(e.getX(), e.getY() + e.getEyeHeight(e.getPose()), e.getZ(), x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return ((ClientPlayerEntity) e).getHungerManager().getFoodLevel();
    }

    public ClientPlayerEntity getRaw() {
        return (ClientPlayerEntity) e;
    }

    public String toString() {
        return "Client" + super.toString();
    }
}
