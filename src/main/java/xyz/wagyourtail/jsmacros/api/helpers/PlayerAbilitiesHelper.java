package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.entity.player.PlayerAbilities;

/**
 * @author Wagyourtail
 * @since 1.0.3
 */
public class PlayerAbilitiesHelper {
	protected PlayerAbilities a;
	
	public PlayerAbilitiesHelper(PlayerAbilities a) {
		this.a = a;
	}
	
	/**
	 * @since 1.0.3
	 * @return whether the player can be damaged.
	 */
	public boolean getInvulnerable() {
		return a.invulnerable;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is currently flying.
	 */
	public boolean getFlying() {
		return a.flying;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is allowed to fly.
	 */
	public boolean getAllowFlying() {
		return a.allowFlying;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is in creative.
	 */
	public boolean getCreativeMode() {
		return a.creativeMode;
	}
	
	/**
	 * set the player flying state.
	 * 
	 * @since 1.0.3
	 * @param b
	 * @return
	 */
	public PlayerAbilitiesHelper setFlying(boolean b) {
		a.flying = b;
		return this;
	}
	
	/**
	 * set the player allow flying state.
	 * 
	 * @since 1.0.3
	 * @param b
	 * @return
	 */
	public PlayerAbilitiesHelper setAllowFlying(boolean b) {
		a.allowFlying = b;
		return this;
	}
	
	/**
	 * @since 1.0.3
	 * @return the player fly speed multiplier.
	 */
	public float getFlySpeed() {
		return a.getFlySpeed();
	}
	
	/**
	 * set the player fly speed multiplier.
	 * 
	 * @since 1.0.3
	 * @param flySpeed
	 * @return
	 */
	public PlayerAbilitiesHelper setFlySpeed(float flySpeed) {
		a.setFlySpeed(flySpeed);
		return this;
	}
	
	public PlayerAbilities getRaw() {
		return a;
	}
}
