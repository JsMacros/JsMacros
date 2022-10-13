package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class LivingEntityHelper<T extends LivingEntity> extends EntityHelper<T> {

    public LivingEntityHelper(T e) {
        super(e);
    }

    /**
     * @since 1.2.7
     * @return entity status effects.
     */
    public List<StatusEffectHelper> getStatusEffects() {
        List<StatusEffectHelper> l = new ArrayList<>();
        for (StatusEffectInstance i : ImmutableList.copyOf(base.getStatusEffects())) {
            l.add(new StatusEffectHelper(i));
        }
        return l;
    }
    
    /**
     * @since 1.2.7
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper
     * @return the item in the entity's main hand.
     */
    public ItemStackHelper getMainHand() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.MAINHAND));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's off hand.
     */
    public ItemStackHelper getOffHand() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.OFFHAND));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's head armor slot.
     */
    public ItemStackHelper getHeadArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.HEAD));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's chest armor slot.
     */
    public ItemStackHelper getChestArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.CHEST));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's leg armor slot.
     */
    public ItemStackHelper getLegArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.LEGS));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's foot armor slot.
     */
    public ItemStackHelper getFootArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.FEET));
    }
    
    /**
     * @since 1.3.1
     * @return entity's health
     */
    public float getHealth() {
        return base.getHealth();
    }

    /**
     * @since 1.6.5
     * @return entity's max health
     */
    public float getMaxHealth() {
        return base.getMaximumHealth();
    }

    /**
     * @since 1.2.7
     * @return if the entity is in a bed.
     */
    public boolean isSleeping() {
        return base.isSleeping();
    }

    /**
     * @since 1.5.0
     * @return if the entity has elytra deployed
     */
    public boolean isFallFlying() {
        return base.isFallFlying();
    }
    
}
