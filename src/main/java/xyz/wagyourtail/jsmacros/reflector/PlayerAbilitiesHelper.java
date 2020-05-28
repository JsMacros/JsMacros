package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.entity.player.PlayerAbilities;

public class PlayerAbilitiesHelper {
	protected PlayerAbilities a;
	
	public PlayerAbilitiesHelper(PlayerAbilities a) {
		this.a = a;
	}
	
	public boolean getInvulnerable() {
		return a.invulnerable;
	}
	
	public boolean getFlying() {
		return a.flying;
	}
	
	public boolean getAllowFlying() {
		return a.allowFlying;
	}
	
	public boolean getCreativeMode() {
		return a.creativeMode;
	}
	
	public float getFlySpeed() {
		return a.getFlySpeed();
	}
	
	public void setFlySpeed(float flySpeed) {
		a.setFlySpeed(flySpeed);
	}
	
	public PlayerAbilities getRaw() {
		return a;
	}
}
