package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BowItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class LivingEntityHelper<T extends LivingEntity> extends EntityHelper<T> {

    public LivingEntityHelper(T e) {
        super(e);
    }

    /**
     * @return entity status effects.
     * @since 1.2.7
     */
    public List<StatusEffectHelper> getStatusEffects() {
        List<StatusEffectHelper> l = new ArrayList<>();
        for (StatusEffectInstance i : ImmutableList.copyOf(base.getStatusEffects())) {
            l.add(new StatusEffectHelper(i));
        }
        return l;
    }

    /**
     * @param effect the status effect
     * @return if the entity can have a certain status effect
     * @since 1.8.4
     */
    private boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return base.canHaveStatusEffect(effect);
    }

    /**
     * @param effect the status effect
     * @return if the entity can have a certain status effect
     * @since 1.8.4
     */
    public boolean canHaveStatusEffect(StatusEffectHelper effect) {
        return canHaveStatusEffect(effect.getRaw());
    }

    /**
     * For client side entities, excluding the player, this will most likely return {@code false}
     * even if the entity has the effect, as effects are not synced to the client.
     *
     * @return {@code true} if the entity has the specified status effect, {@code false} otherwise.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: StatusEffectId")
    public boolean hasStatusEffect(String id) {
        StatusEffect effect = Registries.STATUS_EFFECT.get(RegistryHelper.parseIdentifier(id));
        return base.getStatusEffects().stream().anyMatch(statusEffectInstance -> statusEffectInstance.getEffectType().equals(effect));
    }

    /**
     * @return the item in the entity's main hand.
     * @see ItemStackHelper
     * @since 1.2.7
     */
    public ItemStackHelper getMainHand() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.MAINHAND));
    }

    /**
     * @return the item in the entity's off hand.
     * @since 1.2.7
     */
    public ItemStackHelper getOffHand() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.OFFHAND));
    }

    /**
     * @return the item in the entity's head armor slot.
     * @since 1.2.7
     */
    public ItemStackHelper getHeadArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.HEAD));
    }

    /**
     * @return the item in the entity's chest armor slot.
     * @since 1.2.7
     */
    public ItemStackHelper getChestArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.CHEST));
    }

    /**
     * @return the item in the entity's leg armor slot.
     * @since 1.2.7
     */
    public ItemStackHelper getLegArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.LEGS));
    }

    /**
     * @return the item in the entity's foot armor slot.
     * @since 1.2.7
     */
    public ItemStackHelper getFootArmor() {
        return new ItemStackHelper(base.getEquippedStack(EquipmentSlot.FEET));
    }

    /**
     * @return entity's health
     * @since 1.3.1
     */
    public float getHealth() {
        return base.getHealth();
    }

    /**
     * @return entity's max health
     * @since 1.6.5
     */
    public float getMaxHealth() {
        return base.getMaxHealth();
    }

    /**
     * @return the entity's absorption amount.
     * @since 1.8.4
     */
    public float getAbsorptionHealth() {
        return base.getAbsorptionAmount();
    }

    /**
     * @return the entity's armor value.
     * @since 1.8.4
     */
    public int getArmor() {
        return base.getArmor();
    }

    /**
     * @return the entity's default health.
     * @since 1.8.4
     */
    public int getDefaultHealth() {
        return base.defaultMaxHealth;
    }

    /**
     * @return the entity's mob category, {@code UNDEAD}, {@code DEFAULT}, {@code ARTHROPOD}, or
     * {@code ILLAGER}, {@code AQUATIC} or {@code UNKNOWN}.
     * @since 1.8.4
     */
    @DocletReplaceReturn("MobCategory")
    @DocletEnumType(name = "MobCategory", type = "'UNDEAD' | 'DEFAULT' | 'ARTHROPOD' | 'ILLAGER' | 'AQUATIC' | 'UNKNOWN'")
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
     * @return if the entity is in a bed.
     * @since 1.2.7
     */
    public boolean isSleeping() {
        return base.isSleeping();
    }

    /**
     * @return if the entity has elytra deployed
     * @since 1.5.0
     */
    public boolean isFallFlying() {
        return base.isFallFlying();
    }

    /**
     * @return if the entity is on the ground
     * @since 1.8.4
     */
    public boolean isOnGround() {
        return base.isOnGround();
    }

    /**
     * @return if the entity can breathe in water
     * @since 1.8.4
     */
    public boolean canBreatheInWater() {
        return base.canBreatheInWater();
    }

    /**
     * @return if the entity has no drag
     * @since 1.8.4
     */
    public boolean hasNoDrag() {
        return base.hasNoDrag();
    }

    /**
     * @return if the entity has no gravity
     * @since 1.8.4
     */
    public boolean hasNoGravity() {
        return base.hasNoGravity();
    }

    /**
     * @param target the target entity
     * @return if the entity can target a target entity
     * @since 1.8.4
     */
    private boolean canTarget(LivingEntity target) {
        return base.canTarget(target);
    }

    /**
     * @param target the target entity
     * @return if the entity can target a target entity
     * @since 1.8.4
     */
    public boolean canTarget(LivingEntityHelper<?> target) {
        return canTarget(target.getRaw());
    }

    /**
     * @return if the entity can take damage
     * @since 1.8.4
     */
    public boolean canTakeDamage() {
        return base.canTakeDamage();
    }

    /**
     * @return if the entity is part of the game (is alive and not spectator)
     * @since 1.8.4
     */
    public boolean isPartOfGame() {
        return base.isPartOfGame();
    }

    /**
     * @return if the entity is in spectator
     * @since 1.8.4
     */
    public boolean isSpectator() {
        return base.isSpectator();
    }

    /**
     * @return if the entity is undead
     * @since 1.8.4
     */
    public boolean isUndead() {
        return base.isUndead();
    }

    /**
     * @return the bow pull progress of the entity, {@code 0} by default.
     * @since 1.8.4
     */
    public double getBowPullProgress() {
        if (base.getMainHandStack().getItem() instanceof BowItem) {
            return BowItem.getPullProgress(base.getItemUseTime());
        } else {
            return 0;
        }
    }

    /**
     * @since 1.9.0
     */
    public int getItemUseTimeLeft() {
        return base.getItemUseTimeLeft();
    }

    /**
     * @return {@code true} if the entity is a baby, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBaby() {
        return base.isBaby();
    }

    /**
     * @param entity the entity to check line of sight to
     * @return {@code true} if the player has line of sight to the specified entity, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canSeeEntity(EntityHelper<?> entity) {
        return canSeeEntity(entity, true);
    }

    /**
     * @param entity     the entity to check line of sight to
     * @param simpleCast whether to use a simple raycast or a more complex one
     * @return {@code true} if the entity has line of sight to the specified entity, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canSeeEntity(EntityHelper<?> entity, boolean simpleCast) {
        Entity rawEntity = entity.getRaw();

        Vec3d baseEyePos = new Vec3d(base.getX(), base.getEyeY(), base.getZ());
        Vec3d vec3d = base.getEyePos();
        Vec3d vec3d2 = base.getRotationVec(1.0F).multiply(10);
        Vec3d vec3d3 = vec3d.add(vec3d2);
        Box box = base.getBoundingBox().stretch(vec3d2).expand(1.0);

        Function<Vec3d, Boolean> canSee = pos -> base.getWorld().raycast(new RaycastContext(baseEyePos, pos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, base)).getType() == HitResult.Type.MISS;

        if (canSee.apply(new Vec3d(rawEntity.getX(), rawEntity.getEyeY(), rawEntity.getZ()))
                || canSee.apply(new Vec3d(rawEntity.getX(), rawEntity.getY() + 0.5, rawEntity.getZ()))
                || canSee.apply(new Vec3d(rawEntity.getX(), rawEntity.getY(), rawEntity.getZ()))) {
            return true;
        }

        if (simpleCast) {
            return false;
        }

        Box boundingBox = rawEntity.getBoundingBox();
        double bHeight = boundingBox.maxY - boundingBox.minY;
        int steps = (int) (bHeight / 0.1);
        double diffX = (boundingBox.maxX - boundingBox.minX) / 2;
        double diffZ = (boundingBox.maxZ - boundingBox.minZ) / 2;
        // Create 4 pillars around the mob to check for visibility
        for (int i = 0; i < steps; i++) {
            double y = i * 0.1;
            if (canSee.apply(new Vec3d(rawEntity.getX() + diffX, rawEntity.getY() + y, rawEntity.getZ()))
                    || canSee.apply(new Vec3d(rawEntity.getX() - diffX, rawEntity.getY() + y, rawEntity.getZ()))
                    || canSee.apply(new Vec3d(rawEntity.getX(), rawEntity.getY() + y, rawEntity.getZ() + diffZ))
                    || canSee.apply(new Vec3d(rawEntity.getX(), rawEntity.getY() + y, rawEntity.getZ() - diffZ))) {
                return true;
            }
        }
        return false;
    }

}
