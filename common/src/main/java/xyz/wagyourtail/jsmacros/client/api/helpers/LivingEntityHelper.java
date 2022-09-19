package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BowItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasStatusEffect(String id) {
        StatusEffect effect = Registry.STATUS_EFFECT.get(new Identifier(id));
        return base.getStatusEffects().stream().anyMatch(statusEffectInstance -> statusEffectInstance.getEffectType().equals(effect));
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
        return base.getMaxHealth();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getAbsorptionHealth() {
        return base.getAbsorptionAmount();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getArmor() {
        return base.getArmor();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getDefaultHealth() {
        return base.defaultMaxHealth;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getMobCategory() {
        EntityGroup group = base.getGroup();
        if (group == EntityGroup.UNDEAD) {
            return "UNDEAD";
        } else if (group == EntityGroup.DEFAULT) {
            return "DEFAULT";
        } else if (group == EntityGroup.ARTHROPOD) {
            return "ARTHROPOD";
        } else if (group == EntityGroup.ILLAGER) {
            return "ILLAGER";
        } else if (group == EntityGroup.AQUATIC) {
            return "AQUATIC";
        } else {
            return "UNKNOWN";
        }
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

    /**
     * @return
     *
     * @since 1.9.0
     */
    public double getBowPullProgress() {
        if (base.getMainHandStack().getItem() instanceof BowItem) {
            return BowItem.getPullProgress(base.getItemUseTime());
        } else {
            return 0;
        }
    }
    
}
