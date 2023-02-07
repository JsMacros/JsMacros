package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity;

import net.minecraft.entity.player.PlayerEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.projectile.FishingBobberEntityHelper;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class PlayerEntityHelper<T extends PlayerEntity> extends LivingEntityHelper<T> {
    
    public PlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * get player's actual name. (not display name)
     * @since 1.8.4
     */
    public String getPlayerName() {
        return base.getGameProfile().getName();
    }



    /**
     * @since 1.0.3
     * @see PlayerAbilitiesHelper
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

    /**
     * @return the fishing bobber of the player, or {@code null} if the player is not fishing.
     *
     * @since 1.8.4
     */
    public FishingBobberEntityHelper getFishingBobber() {
        return base.fishHook == null ? null : new FishingBobberEntityHelper(base.fishHook);
    }

    /**
     * @since 1.8.4
     * @return
     */
    public float getAttackCooldownProgress() {
        return base.getAttackCooldownProgress(0);
    }

    /**
     * @since 1.8.4
     * @return
     */
    public float getAttackCooldownProgressPerTick() {
        return base.getAttackCooldownProgressPerTick();
    }

    /**
     * @return the player's score.
     *
     * @since 1.8.4
     */
    public int getScore() {
        return base.getScore();
    }
    
}
