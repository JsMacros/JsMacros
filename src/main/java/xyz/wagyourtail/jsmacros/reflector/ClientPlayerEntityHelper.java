package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ClientPlayerEntityHelper extends PlayerEntityHelper {

	public ClientPlayerEntityHelper(ClientPlayerEntity e) {
		super(e);
	}
	
	public void lookAt(float pitch, float yaw) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        MinecraftClient mc = MinecraftClient.getInstance();
       e.prevPitch =e.pitch;
       e.prevYaw =e.yaw;
       e.pitch = pitch;
       e.yaw = yaw;
        if (mc.player.getVehicle() != null) {
           e.getVehicle().onPassengerLookAround(mc.player);
        }
    }
	
	public int getFoodLevel() {
	    return ((ClientPlayerEntity)e).getHungerManager().getFoodLevel();
	}
	
	public ItemStackHelper getMainHand() {
	    return new ItemStackHelper(((ClientPlayerEntity) e).inventory.getMainHandStack());
	}
	
	public ItemStackHelper getOffHand() {
	    return new ItemStackHelper(((ClientPlayerEntity) e).inventory.offHand.get(0));
	}
	
	public ItemStackHelper getHeadArmor() {
	    return new ItemStackHelper(((ClientPlayerEntity) e).inventory.getArmorStack(3));
	}
	
	public ItemStackHelper getChestArmor() {
        return new ItemStackHelper(((ClientPlayerEntity) e).inventory.getArmorStack(2));
    }
	
	public ItemStackHelper getLegArmor() {
        return new ItemStackHelper(((ClientPlayerEntity) e).inventory.getArmorStack(1));
    }
	
	public ItemStackHelper getFootArmor() {
        return new ItemStackHelper(((ClientPlayerEntity) e).inventory.getArmorStack(0));
    }
	
	public ClientPlayerEntity getRaw() {
        return (ClientPlayerEntity) e;
    }
	
	public String toString() {
	    return "Client"+super.toString();
	}
}
