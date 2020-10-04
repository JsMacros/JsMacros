package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * @author Wagyourtail
 * @since 1.0.3
 * @see xyz.wagyourtail.jsmacros.api.helpers.PlayerEntityHelper
 */
public class ClientPlayerEntityHelper extends PlayerEntityHelper {

	public ClientPlayerEntityHelper(ClientPlayerEntity e) {
		super(e);
	}
	
	/**
	 * @since 1.0.3
	 * 
	 * @param yaw (was pitch prior to 1.2.6)
	 * @param pitch (was yaw prior to 1.2.6)
	 * @return
	 */
	public ClientPlayerEntityHelper lookAt(float yaw, float pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        e.prevPitch =e.pitch;
        e.prevYaw =e.yaw;
        e.pitch = pitch;
        e.yaw = MathHelper.fwrapDegrees(yaw);
        if (e.getVehicle() != null) {
           e.getVehicle().onPassengerLookAround(e);
        }
        return this;
    }
	
	/**
	 * @since 1.1.2
	 * 
	 * @return
	 */
	public int getFoodLevel() {
	    return ((ClientPlayerEntity)e).getHungerManager().getFoodLevel();
	}
	
	public ClientPlayerEntity getRaw() {
        return (ClientPlayerEntity) e;
    }
	
	public String toString() {
	    return "Client"+super.toString();
	}
}
