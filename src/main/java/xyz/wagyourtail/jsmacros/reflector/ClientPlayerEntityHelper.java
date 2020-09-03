package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ClientPlayerEntityHelper extends PlayerEntityHelper {

	public ClientPlayerEntityHelper(ClientPlayerEntity e) {
		super(e);
	}
	
	public ClientPlayerEntityHelper lookAt(float yaw, float pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        MinecraftClient mc = MinecraftClient.getInstance();
        e.prevPitch =e.pitch;
        e.prevYaw =e.yaw;
        e.pitch = pitch;
        e.yaw = MathHelper.fwrapDegrees(yaw);
        if (e.getVehicle() != null) {
           e.getVehicle().onPassengerLookAround(e);
        }
        return this;
    }
	
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
