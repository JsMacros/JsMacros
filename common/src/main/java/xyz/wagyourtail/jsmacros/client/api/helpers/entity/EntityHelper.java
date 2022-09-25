package xyz.wagyourtail.jsmacros.client.api.helpers.entity;

import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;
import xyz.wagyourtail.jsmacros.client.api.helpers.ChunkHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.DirectionHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
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
     * @return entity block position.
     *
     * @since 1.6.5
     */
    public BlockPosHelper getBlockPos() {
        return new BlockPosHelper(base.getBlockPos());
    }

    /**
     * @return entity chunk coordinates. Since Pos2D only has x and y fields, z coord is y.
     *
     * @since 1.6.5
     */
    public PositionCommon.Pos2D getChunkPos() {
        return new PositionCommon.Pos2D(base.getChunkPos().x, base.getChunkPos().z);
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
     * @return {@code true} if the entity is sneaking, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSneaking() {
        return base.isSneaking();
    }

    /**
     * @return {@code true} if the entity is sprinting, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSprinting() {
        return base.isSprinting();
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
    public EntityHelper<T> setCustomName(TextHelper name) {
        if (name == null) {
            base.setCustomName(null);
        } else {
            base.setCustomName(name.getRaw());
        }
        return this;
    }

    /**
     * sets the name to always display
     * @since 1.8.0
     * @param b
     */
    public EntityHelper<T> setCustomNameVisible(boolean b) {
        base.setCustomNameVisible(b);
        return this;
    }

    /**
     * @param color
     */
    public EntityHelper<T> setGlowingColor(int color) {
        ((IMixinEntity) base).jsmacros_setGlowingColor(color);
        return this;
    }

    /**
     *
     */
    public EntityHelper<T> resetGlowingColor() {
        ((IMixinEntity) base).jsmacros_resetColor();
        return this;
    }

    /**
     * warning: affected by setGlowingColor
     * @since 1.8.2
     * @return glow color
     */
    public int getGlowingColor() {
        return base.getTeamColorValue();
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

    /**
     * @return the maximum amount of air this entity can have.
     *
     * @since 1.8.4
     */
    public int getMaxAir() {
        return base.getMaxAir();
    }

    /**
     * @return the amount of air this entity has.
     *
     * @since 1.8.4
     */
    public int getAir() {
        return base.getAir();
    }
    
    /**
     * @return this entity's current speed in blocks per second.
     *
     * @since 1.8.4
     */
    public double getSpeed() {
        double dx = Math.abs(base.getX() - base.prevX);
        double dz = Math.abs(base.getZ() - base.prevZ);
        return Math.sqrt(dx * dx + dz * dz) * 20;
    }

    /**
     * @return the direction the entity is facing, rounded to the nearest 45 degrees.
     *
     * @since 1.8.4
     */
    public DirectionHelper getFacingDirection() {
        return new DirectionHelper(base.getHorizontalFacing());
    }

    /**
     * @return the distance between this entity and the specified one.
     *
     * @since 1.8.4
     */
    public float distanceTo(EntityHelper<?> entity) {
        return base.distanceTo(entity.getRaw());
    }

    /**
     * @return the distance between this entity and the specified position.
     *
     * @since 1.8.4
     */
    public double distanceTo(BlockPosHelper pos) {
        return Math.sqrt(pos.getRaw().getSquaredDistance(base.getPos()));
    }

    /**
     * @return the distance between this entity and the specified position.
     *
     * @since 1.8.4
     */
    public double distanceTo(PositionCommon.Pos3D pos) {
        return Math.sqrt(base.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()));
    }

    /**
     * @return the distance between this entity and the specified position.
     *
     * @since 1.8.4
     */
    public double distanceTo(double x, double y, double z) {
        return Math.sqrt(base.squaredDistanceTo(x, y, z));
    }

    /**
     * @return the velocity vector.
     *
     * @since 1.8.4
     */
    public PositionCommon.Pos3D getVelocity() {
        return new PositionCommon.Pos3D(base.getVelocity().x, base.getVelocity().y, base.getVelocity().z);
    }

    /**
     * @return the chunk helper for the chunk this entity is in.
     *
     * @since 1.8.4
     */
    public ChunkHelper getChunk() {
        return new ChunkHelper(base.getWorld().getChunk(base.getBlockPos()));
    }
    
    /**
     * @return the name of the biome this entity is in.
     *
     * @since 1.8.4
     */
    public String getBiome() {
        return MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).getId(MinecraftClient.getInstance().world.getBiome(base.getBlockPos()).value()).toString();
    }

    public String toString() {
        return String.format("Entity:{\"name\": \"%s\", \"type\": \"%s\"}", this.getName(), this.getType());
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

    /**
     * @return the entity as a server entity if an integrated server is running and {@code null} otherwise.
     *
     * @since 1.8.4
     */
    public EntityHelper<?> asServerEntity() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isIntegratedServerRunning()) {
            return null;
        }
        Entity entity = client.getServer().getPlayerManager().getPlayer(client.player.getUuid()).getWorld().getEntity(base.getUuid());
        if (entity == null) {
            return null;
        } else {
            return create(entity);
        }
    }
    
}
