package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.entity.player.PlayerEntity;

/**
 * @author Wagyourtail
 */
public class PlayerEntityHelper extends LivingEntityHelper {
    
    public PlayerEntityHelper(PlayerEntity e) {
        super(e);
    }
    
    /**
     * @since 1.0.3
     * @see xyz.wagyourtail.jsmacros.api.helpers.PlayerAbilitiesHelper
     * @return
     */
    public PlayerAbilitiesHelper getAbilities() {
    	return new PlayerAbilitiesHelper(((PlayerEntity)e).abilities);
    }
    
    
    /**
     * @since 1.2.0
     */
    public ItemStackHelper getMainHand() {
        return super.getMainHand();
    }
    
    /**
     * @since 1.2.0
     */
    public ItemStackHelper getOffHand() {
        return super.getOffHand();
    }
    
    /**
     * @since 1.2.0
     */
    public ItemStackHelper getHeadArmor() {
        return super.getHeadArmor();
    }
    
    /**
     * @since 1.2.0
     */
    public ItemStackHelper getChestArmor() {
        return super.getChestArmor();
    }
    
    /**
     * @since 1.2.0
     */
    public ItemStackHelper getLegArmor() {
        return super.getLegArmor();
    }
    
    /**
     * @since 1.2.0
     */
    public ItemStackHelper getFootArmor() {
        return super.getFootArmor();
    }
    
    /**
     * @since 1.2.5 [citation needed]
     * @return
     */
    public int getXP() {
        return ((PlayerEntity)e).experienceLevel;
    }
    
    /**
     * @since 1.2.5 [citation needed]
     * @return
     */
    public boolean isSleeping() {
        return super.isSleeping();
    }
    
    /**
     * @since 1.2.5 [citation needed]
     * @return if the player has slept the minimum ammount of time to pass the night.
     */
    public boolean isSleepingLongEnough() {
        return ((PlayerEntity)e).isSleepingLongEnough();
    }
    
    public PlayerEntity getRaw() {
        return (PlayerEntity) e;
    }
    
    public String toString() {
        return "Player"+super.toString();
    }
}
