package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.player.PlayerEntity;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class PlayerEntityHelper<T extends PlayerEntity> extends LivingEntityHelper<T> {
    
    public PlayerEntityHelper(T e) {
        super(e);
    }
    
    /**
     * @since 1.0.3
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerAbilitiesHelper
     * @return
     */
    public PlayerAbilitiesHelper getAbilities() {
    	return new PlayerAbilitiesHelper(base.getAbilities());
    }
    
    
    /**
     * @since 1.2.0
     */
    @Override
    public ItemStackHelper getMainHand() {
        return super.getMainHand();
    }
    
    /**
     * @since 1.2.0
     */
    @Override
    public ItemStackHelper getOffHand() {
        return super.getOffHand();
    }
    
    /**
     * @since 1.2.0
     */
    @Override
    public ItemStackHelper getHeadArmor() {
        return super.getHeadArmor();
    }
    
    /**
     * @since 1.2.0
     */
    @Override
    public ItemStackHelper getChestArmor() {
        return super.getChestArmor();
    }
    
    /**
     * @since 1.2.0
     */
    @Override
    public ItemStackHelper getLegArmor() {
        return super.getLegArmor();
    }
    
    /**
     * @since 1.2.0
     */
    @Override
    public ItemStackHelper getFootArmor() {
        return super.getFootArmor();
    }
    
    /**
     * @since 1.2.5 [citation needed]
     * @return
     */
    public int getXP() {
        return base.totalExperience;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public int getXPLevel() {
        return base.experienceLevel;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public float getXPProgress() {
        return base.experienceProgress;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public int getXPToLevelUp() {
        return base.getNextLevelExperience();
    }
    
    /**
     * @since 1.2.5 [citation needed]
     * @return
     */
    @Override
    public boolean isSleeping() {
        return super.isSleeping();
    }
    
    /**
     * @since 1.2.5 [citation needed]
     * @return if the player has slept the minimum ammount of time to pass the night.
     */
    public boolean isSleepingLongEnough() {
        return base.canResetTimeBySleeping();
    }
    
    public String toString() {
        return "Player"+super.toString();
    }
}
