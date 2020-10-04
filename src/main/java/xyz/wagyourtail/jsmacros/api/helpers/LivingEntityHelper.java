package xyz.wagyourtail.jsmacros.api.helpers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public class LivingEntityHelper extends EntityHelper {

    public LivingEntityHelper(LivingEntity e) {
        super(e);
    }

    /**
     * @return entity status effects.
     */
    public List<StatusEffectHelper> getStatusEffects() {
        List<StatusEffectHelper> l = new ArrayList<>();
        for (StatusEffectInstance i : ImmutableList.copyOf(((LivingEntity) e).getStatusEffects())) {
            l.add(new StatusEffectHelper(i));
        }
        return l;
    }
    
    /**
     * @since 1.2.7
     * @see xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper
     * @return the item in the entity's main hand.
     */
    public ItemStackHelper getMainHand() {
        return new ItemStackHelper(((LivingEntity) e).getEquippedStack(EquipmentSlot.MAINHAND));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's off hand.
     */
    public ItemStackHelper getOffHand() {
        return new ItemStackHelper(((LivingEntity) e).getEquippedStack(EquipmentSlot.OFFHAND));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's head armor slot.
     */
    public ItemStackHelper getHeadArmor() {
        return new ItemStackHelper(((LivingEntity) e).getEquippedStack(EquipmentSlot.HEAD));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's chest armor slot.
     */
    public ItemStackHelper getChestArmor() {
        return new ItemStackHelper(((LivingEntity) e).getEquippedStack(EquipmentSlot.CHEST));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's leg armor slot.
     */
    public ItemStackHelper getLegArmor() {
        return new ItemStackHelper(((LivingEntity) e).getEquippedStack(EquipmentSlot.LEGS));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's foot armor slot.
     */
    public ItemStackHelper getFootArmor() {
        return new ItemStackHelper(((LivingEntity) e).getEquippedStack(EquipmentSlot.FEET));
    }
    
    /**
     * @since 1.2.7
     * @return if the entity is in a bed.
     */
    public boolean isSleeping() {
        return ((LivingEntity) e).isSleeping();
    }
    
}
