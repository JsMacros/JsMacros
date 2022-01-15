package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class EntityHelper<T extends Entity> extends BaseHelper<T> {
    
    protected EntityHelper(T e) {
        super(e);
    }
    
    /**
     * @return entity position.
     */
    public PositionCommon.Pos3D getPos() {
        return new PositionCommon.Pos3D(base.getX(), base.getY(), base.getZ());
    }
    
    /**
     * @since 1.0.8
     * @return the {@code x} value of the entity.
     */
    public double getX() {
        return base.getX();
    }

    /**
     * @since 1.0.8
     * @return the {@code y} value of the entity.
     */
    public double getY() {
        return base.getY();
    }
    
    /**
     * @since 1.0.8
     * @return the {@code z} value of the entity.
     */
    public double getZ() {
        return base.getZ();
    }

    /**
     * @since 1.2.8
     * @return the current eye height offset for the entitye.
     */
    public double getEyeHeight() {
        return base.getEyeHeight(base.getPose());
    }

    /**
     * @since 1.0.8
     * @return the {@code pitch} value of the entity.
     */
    public float getPitch() {
        return base.getPitch();
    }
    
    /**
     * @since 1.0.8
     * @return the {@code yaw} value of the entity.
     */
    public float getYaw() {
        return MathHelper.wrapDegrees(base.getYaw());
    }
    
    /**
     * @return the name of the entity.
     * @since 1.0.8 [citation needed], returned string until 1.6.4
     */
    public TextHelper getName() {
        return new TextHelper(base.getName());
    }
    
    /**
     * @return the type of the entity.
     */
    public String getType() {
        return EntityType.getId(base.getType()).toString();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity has the glowing effect.
     */
    public boolean isGlowing() {
        return base.isGlowing();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity is in lava.
     */
    public boolean isInLava() {
        return base.isInLava();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity is on fire.
     */
    public boolean isOnFire() {
        return base.isOnFire();
    }
    
    /**
     * @since 1.1.8 [citation needed]
     * @return the vehicle of the entity.
     */
    public EntityHelper<?> getVehicle() {
        Entity parent = base.getVehicle();
        if (parent != null) return EntityHelper.create(parent);
        return null;
    }
    
    /**
     * @since 1.1.8 [citation needed]
     * @return the entity passengers.
     */
    public List<EntityHelper<?>> getPassengers() {
        List<EntityHelper<?>> entities = base.getPassengerList().stream().map(EntityHelper::create).collect(Collectors.toList());
        return entities.size() == 0 ? null : entities;
        
    }
    
    /**
     * @since 1.2.8, was a {@link String} until 1.5.0
     * @return
     */
    public NBTElementHelper<?> getNBT() {
        NbtCompound nbt = new NbtCompound();
        base.writeNbt(nbt);
        return NBTElementHelper.resolve(nbt);
    }

    /**
     * @since 1.6.4
     * @param name
     */
    public void setCustomName(TextHelper name) {
        base.setCustomName(name.getRaw());
    }

    /**
     * @param color
     */
    public void setGlowingColor(int color) {
        ((IMixinEntity) base).jsmacros_setGlowingColor(color);
    }

    /**
     *
     */
    public void resetGlowingColor() {
        ((IMixinEntity) base).jsmacros_resetColor();
    }

    /**
     * Sets whether the entity is glowing.
     * @since 1.1.9
     * @param val
     * @return
     */
    public EntityHelper<T> setGlowing(boolean val) {
        ((IMixinEntity) base).jsmacros_setForceGlowing(val ? 2 : 0);
        return this;
    }

    /**
     * reset the glowing effect to proper value.
     * @since 1.6.3
     * @return
     */
    public EntityHelper<T> resetGlowing() {
        ((IMixinEntity) base).jsmacros_setForceGlowing(1);
        return this;
    }
    
    /**
     * Checks if the entity is still alive.
     * @since 1.2.8
     * @return
     */
    public boolean isAlive() {
        return base.isAlive();
    }

    /**
    * @since 1.6.5
    * @return UUID of the entity, random* if not a player, otherwise the player's uuid.
    */
    public String getUUID() {
        return base.getUuid().toString();
    }

    public String toString() {
        return String.format("Entity:{\"name\":\"%s\", \"type\":\"%s\"}", this.getName(), this.getType());
    }


    /**
     * mostly for internal use.
     *
     * @param e mc entity.
     *
     * @return correct subclass of this.
     */
    public static EntityHelper<?> create(Entity e) {
        if (e instanceof ClientPlayerEntity) return new ClientPlayerEntityHelper<>((ClientPlayerEntity) e);
        if (e instanceof PlayerEntity) return new PlayerEntityHelper<>((PlayerEntity) e);
        if (e instanceof VillagerEntity) return new VillagerEntityHelper((VillagerEntity) e);
        if (e instanceof MerchantEntity) return new MerchantEntityHelper<>((MerchantEntity) e);
        if (e instanceof LivingEntity) return new LivingEntityHelper<>((LivingEntity) e);
        if (e instanceof ItemEntity) return new ItemEntityHelper((ItemEntity) e);
        return new EntityHelper<>(e);
    }

    /**
     * @since 1.6.3
     * @return cast of this entity helper (mainly for typescript)
     */
    public ClientPlayerEntityHelper<?> asClientPlayer() {
        return (ClientPlayerEntityHelper<?>) this;
    }

    /**
     * @since 1.6.3
     * @return cast of this entity helper (mainly for typescript)
     */
    public PlayerEntityHelper<?> asPlayer() {
        return (PlayerEntityHelper<?>) this;
    }

    /**
     * @since 1.6.3
     * @return cast of this entity helper (mainly for typescript)
     */
     public VillagerEntityHelper asVillager() {
         return (VillagerEntityHelper) this;
     }

    /**
     * @since 1.6.3
     * @return cast of this entity helper (mainly for typescript)
     */
    public MerchantEntityHelper<?> asMerchant() {
        return (MerchantEntityHelper<?>) this;
    }


    /**
     * @since 1.6.3
     * @return cast of this entity helper (mainly for typescript)
     */
    public LivingEntityHelper<?> asLiving() {
        return (LivingEntityHelper<?>) this;
    }

    /**
     * @since 1.6.3
     * @return cast of this entity helper (mainly for typescript)
     */
    public ItemEntityHelper asItem() {
        return (ItemEntityHelper) this;
    }
}