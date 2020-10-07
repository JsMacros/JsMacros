package xyz.wagyourtail.jsmacros.api.helpers;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import xyz.wagyourtail.jsmacros.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 *
 */
public class EntityHelper {
    protected Entity e;
    
    public EntityHelper(Entity e) {
        this.e = e;
    }
    
    /**
     * @return entity position.
     */
    public PositionCommon.Pos3D getPos() {
        return new PositionCommon.Pos3D(e.getX(), e.getY(), e.getZ());
    }
    
    /**
     * @since 1.0.8
     * @return the {@code x} value of the entity.
     */
    public double getX() {
        return e.getX();
    }

    /**
     * @since 1.0.8
     * @return the {@code y} value of the entity.
     */
    public double getY() {
        return e.getY();
    }
    
    /**
     * @since 1.0.8
     * @return the {@code z} value of the entity.
     */
    public double getZ() {
        return e.getZ();
    }
    
    /**
     * @since 1.0.8
     * @return the {@code pitch} value of the entity.
     */
    public float getPitch() {
        return e.pitch;
    }
    
    /**
     * @since 1.0.8
     * @return the {@code yaw} value of the entity.
     */
    public float getYaw() {
        return MathHelper.fwrapDegrees(e.yaw);
    }
    
    /**
     * @return the name of the entity.
     */
    public String getName() {
        return e.getName().getString();
    }
    
    /**
     * @return the type of the entity.
     */
    public String getType() {
        return EntityType.getId(e.getType()).toString();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity has the glowing effect.
     */
    public boolean isGlowing() {
        return e.isGlowing();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity is in lava.
     */
    public boolean isInLava() {
        return e.isInLava();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity is on fire.
     */
    public boolean isOnFire() {
        return e.isOnFire();
    }
    
    /**
     * @since 1.1.8 [citation needed]
     * @return the vehicle of the entity.
     */
    public EntityHelper getVehicle() {
        Entity parent = e.getVehicle();
        if (parent != null) return new EntityHelper(parent);
        return null;
    }
    
    /**
     * @since 1.1.8 [citation needed]
     * @return the entity passengers.
     */
    public List<EntityHelper> getPassengers() {
        List<EntityHelper> entities = e.getPassengerList().stream().map((e) -> new EntityHelper(e)).collect(Collectors.toList());
        return entities.size() == 0 ? null : entities;
        
    }
    
    /**
     * @since 1.2.8
     * @return
     */
    public String getNBT() {
        return e.toTag(new CompoundTag()).toString();
    }
    
    /**
     * Sets whether the entity is glowing.
     * @since 1.1.9
     * @param val
     * @return
     */
    public EntityHelper setGlowing(boolean val) {
        e.setGlowing(val);
        return this;
    }
    
    public Entity getRaw() {
        return e;
    }
    
    public String toString() {
        return String.format("Entity:{\"name\":\"%s\", \"type\":\"%s\"}", this.getName(), this.getType());
    }
    
    public static EntityHelper create(Entity e) {
        if (e instanceof ClientPlayerEntity) return new ClientPlayerEntityHelper((ClientPlayerEntity) e);
        if (e instanceof PlayerEntity) return new PlayerEntityHelper((PlayerEntity) e);
        if (e instanceof LivingEntity) return new LivingEntityHelper((LivingEntity) e);
        return new EntityHelper(e);
    }
}