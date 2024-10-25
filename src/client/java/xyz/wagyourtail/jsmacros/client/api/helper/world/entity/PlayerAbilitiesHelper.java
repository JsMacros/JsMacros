package xyz.wagyourtail.jsmacros.client.api.helper.world.entity;

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
     * @return whether the player can be damaged.
     * @since 1.0.3
     */
    public boolean getInvulnerable() {
        return base.invulnerable;
    }

    /**
     * @return if the player is currently flying.
     * @since 1.0.3
     */
    public boolean getFlying() {
        return base.flying;
    }

    /**
     * @return if the player is allowed to fly.
     * @since 1.0.3
     */
    public boolean getAllowFlying() {
        return base.allowFlying;
    }

    /**
     * @return if the player is in creative.
     * @since 1.0.3
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
     * @since 1.8.4
     */
    public boolean canModifyWorld() {
        return base.allowModifyWorld;
    }

    /**
     * set the player flying state.
     *
     * @param b
     * @return
     * @since 1.0.3
     */
    public PlayerAbilitiesHelper setFlying(boolean b) {
        base.flying = b;
        return this;
    }

    /**
     * set the player allow flying state.
     *
     * @param b
     * @return
     * @since 1.0.3
     */
    public PlayerAbilitiesHelper setAllowFlying(boolean b) {
        base.allowFlying = b;
        return this;
    }

    /**
     * @return the player fly speed multiplier.
     * @since 1.0.3
     */
    public float getFlySpeed() {
        return base.getFlySpeed();
    }

    /**
     * set the player fly speed multiplier.
     *
     * @param flySpeed
     * @return
     * @since 1.0.3
     */
    public PlayerAbilitiesHelper setFlySpeed(double flySpeed) {
        base.setFlySpeed((float) flySpeed);
        return this;
    }

    /**
     * @return the player's walk speed.
     * @since 1.8.4
     */
    public float getWalkSpeed() {
        return base.getWalkSpeed();
    }

    /**
     * @param speed the new walk speed
     * @return self for chaining.
     * @since 1.8.4
     */
    public PlayerAbilitiesHelper setWalkSpeed(double speed) {
        base.setWalkSpeed((float) speed);
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
