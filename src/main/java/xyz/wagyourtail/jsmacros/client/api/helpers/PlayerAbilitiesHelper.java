package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.player.PlayerAbilities;

/**
 * @author Wagyourtail
 * @since 1.0.3
 */
public class PlayerAbilitiesHelper extends BaseHelper<PlayerAbilities> {
	
	public PlayerAbilitiesHelper(PlayerAbilities a) {
		super(a);
	}
	
	/**
	 * @since 1.0.3
	 * @return whether the player can be damaged.
	 */
	public boolean getInvulnerable() {
		return base.invulnerable;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is currently flying.
	 */
	public boolean getFlying() {
		return base.flying;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is allowed to fly.
	 */
	public boolean getAllowFlying() {
		return base.allowFlying;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is in creative.
	 */
	public boolean getCreativeMode() {
		return base.creativeMode;
	}
	
	/**
	 * set the player flying state.
	 * 
	 * @since 1.0.3
	 * @param b
	 * @return
	 */
	public PlayerAbilitiesHelper setFlying(boolean b) {
		base.flying = b;
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
		base.allowFlying = b;
		return this;
	}
	
	/**
	 * @since 1.0.3
	 * @return the player fly speed multiplier.
	 */
	public float getFlySpeed() {
		return base.getFlySpeed();
	}
	
	/**
	 * set the player fly speed multiplier.
	 * 
	 * @since 1.0.3
	 * @param flySpeed
	 * @return
	 */
	public PlayerAbilitiesHelper setFlySpeed(float flySpeed) {
		base.setFlySpeed(flySpeed);
		return this;
	}
}
