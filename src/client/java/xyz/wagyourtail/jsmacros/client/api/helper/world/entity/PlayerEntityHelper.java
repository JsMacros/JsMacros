package xyz.wagyourtail.jsmacros.client.api.helper.world.entity;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile.FishingBobberEntityHelper;

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
     *
     * @since 1.8.4
     */
    public String getPlayerName() {
        return base.getGameProfile().getName();
    }

    /**
     * @return
     * @see PlayerAbilitiesHelper
     * @since 1.0.3
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
     * @return
     * @since 1.2.5 [citation needed]
     */
    public int getXP() {
        return base.totalExperience;
    }

    /**
     * @return
     * @since 1.6.5
     */
    public int getXPLevel() {
        return base.experienceLevel;
    }

    /**
     * @return
     * @since 1.6.5
     */
    public float getXPProgress() {
        return base.experienceProgress;
    }

    /**
     * @return
     * @since 1.6.5
     */
    public int getXPToLevelUp() {
        return base.getNextLevelExperience();
    }

    /**
     * @return
     * @since 1.2.5 [citation needed]
     */
    @Override
    public boolean isSleeping() {
        return super.isSleeping();
    }

    /**
     * @return if the player has slept the minimum amount of time to pass the night.
     * @since 1.2.5 [citation needed]
     */
    public boolean isSleepingLongEnough() {
        return base.canResetTimeBySleeping();
    }

    /**
     * @return the fishing bobber of the player, or {@code null} if the player is not fishing.
     * @since 1.8.4
     */
    @Nullable
    public FishingBobberEntityHelper getFishingBobber() {
        return base.fishHook == null ? null : new FishingBobberEntityHelper(base.fishHook);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public float getAttackCooldownProgress() {
        return base.getAttackCooldownProgress(0);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public float getAttackCooldownProgressPerTick() {
        return base.getAttackCooldownProgressPerTick();
    }

    /**
     * @return the player's score.
     * @since 1.8.4
     */
    public int getScore() {
        return base.getScore();
    }

}
