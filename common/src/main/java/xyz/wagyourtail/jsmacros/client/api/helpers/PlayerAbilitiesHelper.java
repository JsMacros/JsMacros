package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.player.PlayerAbilities;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.0.3
 */
@SuppressWarnings("unused")
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
     * Even if this method returns true, the player may not be able to modify the world due to other
     * restrictions such as plugins and mods. Modifying the world includes, placing, breaking or
     * interacting with blocks.
     *
     * @return {@code true} if the player is allowed to modify the world, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean canModifyWorld() {
        return base.allowModifyWorld;
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
    public PlayerAbilitiesHelper setFlySpeed(double flySpeed) {
        base.setFlySpeed((float)flySpeed);
        return this;
    }

    /**
     * @return the player's walk speed.
     *
     * @since 1.8.4
     */
    public float getWalkSpeed() {
        return base.getWalkSpeed();
    }

    /**
     * @param speed the new walk speed
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public PlayerAbilitiesHelper setWalkSpeed(float speed) {
        base.setWalkSpeed(speed);
        return this;
    }

    @Override
    public String toString() {
        return "PlayerAbilitiesHelper:{"
                + "\"invulnerable\": " + base.invulnerable
                + ", \"creativeMode\": " + base.creativeMode
                + ", \"modifyWorld\": " + base.allowModifyWorld
                + ", \"flying\": " + base.flying
                + ", \"allowFlying\": " + base.allowFlying
                + ", \"flySpeed\": " + base.getFlySpeed()
                + ", \"walkSpeed\": " + base.getWalkSpeed()
                + "}";
    }
    
}
