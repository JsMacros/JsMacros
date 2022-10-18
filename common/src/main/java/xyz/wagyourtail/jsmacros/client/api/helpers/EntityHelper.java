package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.boss.EnderDragonEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.boss.WitherEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.decoration.ArmorStandEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.decoration.EndCrystalEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.decoration.ItemFrameEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.decoration.PaintingEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.AbstractPiglinEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.BlazeEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.CreeperEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.DrownedEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.EndermanEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.GhastEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.GuardianEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.IllagerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.PhantomEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.PiglinEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.PillagerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.ShulkerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.SlimeEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.SpellcastingIllagerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.SpiderEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.VexEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.VindicatorEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.WardenEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.WitchEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.ZombieEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob.ZombieVillagerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.AbstractHorseEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.AllayEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.AnimalEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.AxolotlEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.BatEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.BeeEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.CatEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.DolphinEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.DonkeyEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.FishEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.FoxEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.FrogEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.GoatEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.HorseEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.IronGolemEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.LlamaEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.MooshroomEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.OcelotEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.PandaEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.ParrotEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.PigEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.PolarBearEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.PufferfishEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.RabbitEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.SheepEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.SnowGolemEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.StriderEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.TameableEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.TropicalFishEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive.WolfEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.projectile.ArrowEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.projectile.FishingBobberEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.projectile.TridentEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.projectile.WitherSkullEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.vehicle.BoatEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.vehicle.FurnaceMinecartEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.vehicle.TntMinecartEntityHelper;
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
     * @return the entity's eye position.
     *
     * @since 1.8.4
     */
    public PositionCommon.Pos3D getEyePos() {
        return new PositionCommon.Pos3D(base.getEyePos());
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

    @Override
    public String toString() {
        return String.format("%s:{\"name\": \"%s\", \"type\": \"%s\"}", getClass().getSimpleName(), this.getName(), this.getType());
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
        if (e instanceof AnimalEntity) return new AnimalEntityHelper<>((AnimalEntity) e);
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
     * @return this helper as an animal entity helper (mainly for typescript).
     *
     * @since 1.8.4
     */
    public LivingEntityHelper<?> asAnimal() {
        return (AnimalEntityHelper<?>) this;
    }

    /**
     * @return a specialized helper for this entity if it exists and self otherwise.
     *
     * @since 1.8.4
     */
    public EntityHelper<?> getSpecialized() {
        if (base instanceof MobEntity) {
            if (base instanceof EnderDragonEntity dragon) {
                return new EnderDragonEntityHelper(dragon);
            } else if (base instanceof WitherEntity wither) {
                return new WitherEntityHelper(wither);
            }

            if (base instanceof AbstractPiglinEntity abstractPiglin) {
                if (base instanceof PiglinEntity piglin) {
                    return new PiglinEntityHelper(piglin);
                } else {
                    return new AbstractPiglinEntityHelper<>(abstractPiglin);
                }
            } else if (base instanceof CreeperEntity creeper) {
                return new CreeperEntityHelper(creeper);
            } else if (base instanceof ZombieEntity zombie) {
                if (base instanceof DrownedEntity drowned) {
                    return new DrownedEntityHelper(drowned);
                } else if (base instanceof ZombieVillagerEntity zombieVillager) {
                    return new ZombieVillagerEntityHelper(zombieVillager);
                } else {
                    return new ZombieEntityHelper<>(zombie);
                }
            } else if (base instanceof EndermanEntity enderman) {
                return new EndermanEntityHelper(enderman);
            } else if (base instanceof GhastEntity ghast) {
                return new GhastEntityHelper(ghast);
            } else if (base instanceof BlazeEntity blaze) {
                return new BlazeEntityHelper(blaze);
            } else if (base instanceof GuardianEntity guardian) {
                return new GuardianEntityHelper(guardian);
            } else if (base instanceof PhantomEntity phantom) {
                return new PhantomEntityHelper(phantom);
            } else if (base instanceof IllagerEntity illager) {
                if (base instanceof VindicatorEntity vindicator) {
                    return new VindicatorEntityHelper(vindicator);
                } else if (base instanceof PillagerEntity pillager) {
                    return new PillagerEntityHelper(pillager);
                } else if (base instanceof SpellcastingIllagerEntity spellcastingIllager) {
                    return new SpellcastingIllagerEntityHelper<>(spellcastingIllager);
                } else {
                    return new IllagerEntityHelper<>(illager);
                }
            } else if (base instanceof ShulkerEntity shulker) {
                return new ShulkerEntityHelper(shulker);
            } else if (base instanceof SlimeEntity slime) {
                return new SlimeEntityHelper(slime);
            } else if (base instanceof SpiderEntity spider) {
                return new SpiderEntityHelper(spider);
            } else if (base instanceof VexEntity vex) {
                return new VexEntityHelper(vex);
            } else if (base instanceof WardenEntity warden) {
                return new WardenEntityHelper(warden);
            } else if (base instanceof WitchEntity witch) {
                return new WitchEntityHelper(witch);
            }

            if (base instanceof AnimalEntity animal) {
                if (base instanceof AbstractHorseEntity abstractHorse) {
                    if (base instanceof HorseEntity horse) {
                        return new HorseEntityHelper(horse);
                    } else if (base instanceof AbstractDonkeyEntity donkey) {
                        if (base instanceof LlamaEntity llama) {
                            return new LlamaEntityHelper<>(llama);
                        } else {
                            return new DonkeyEntityHelper<>(donkey);
                        }
                    } else {
                        return new AbstractHorseEntityHelper<>(abstractHorse);
                    }
                } else if (base instanceof AxolotlEntity axolotl) {
                    return new AxolotlEntityHelper(axolotl);
                } else if (base instanceof BeeEntity bee) {
                    return new BeeEntityHelper(bee);
                } else if (base instanceof FoxEntity fox) {
                    return new FoxEntityHelper(fox);
                } else if (base instanceof FrogEntity frog) {
                    return new FrogEntityHelper(frog);
                } else if (base instanceof GoatEntity goat) {
                    return new GoatEntityHelper(goat);
                } else if (base instanceof MooshroomEntity mooshroom) {
                    return new MooshroomEntityHelper(mooshroom);
                } else if (base instanceof OcelotEntity ocelot) {
                    return new OcelotEntityHelper(ocelot);
                } else if (base instanceof PandaEntity panda) {
                    return new PandaEntityHelper(panda);
                } else if (base instanceof PigEntity pig) {
                    return new PigEntityHelper(pig);
                } else if (base instanceof PolarBearEntity polarBear) {
                    return new PolarBearEntityHelper(polarBear);
                } else if (base instanceof RabbitEntity rabbit) {
                    return new RabbitEntityHelper(rabbit);
                } else if (base instanceof SheepEntity sheep) {
                    return new SheepEntityHelper(sheep);
                } else if (base instanceof StriderEntity strider) {
                    return new StriderEntityHelper(strider);
                } else if (base instanceof TameableEntity tameable) {
                    if (base instanceof CatEntity cat) {
                        return new CatEntityHelper(cat);
                    } else if (base instanceof WolfEntity wolf) {
                        return new WolfEntityHelper(wolf);
                    } else if (base instanceof ParrotEntity parrot) {
                        return new ParrotEntityHelper(parrot);
                    } else {
                        return new TameableEntityHelper<>(tameable);
                    }
                } else {
                    return new AnimalEntityHelper<>(animal);
                }
            }

            if (base instanceof AllayEntity allay) {
                return new AllayEntityHelper(allay);
            } else if (base instanceof BatEntity bat) {
                return new BatEntityHelper(bat);
            } else if (base instanceof DolphinEntity dolphin) {
                return new DolphinEntityHelper(dolphin);
            } else if (base instanceof IronGolemEntity ironGolem) {
                return new IronGolemEntityHelper(ironGolem);
            } else if (base instanceof SnowGolemEntity snowGolem) {
                return new SnowGolemEntityHelper(snowGolem);
            } else if (base instanceof FishEntity fish) {
                if (base instanceof PufferfishEntity pufferfish) {
                    return new PufferfishEntityHelper(pufferfish);
                } else if (base instanceof TropicalFishEntity tropicalFish) {
                    return new TropicalFishEntityHelper(tropicalFish);
                } else {
                    return new FishEntityHelper<>(fish);
                }
            }
        }

        if (base instanceof ProjectileEntity) {
            if (base instanceof ArrowEntity arrow) {
                return new ArrowEntityHelper(arrow);
            } else if (base instanceof FishingBobberEntity fishingBobber) {
                return new FishingBobberEntityHelper(fishingBobber);
            } else if (base instanceof TridentEntity trident) {
                return new TridentEntityHelper(trident);
            } else if (base instanceof WitherSkullEntity witherSkull) {
                return new WitherSkullEntityHelper(witherSkull);
            }
        }

        if (base instanceof ArmorStandEntity armorStand) {
            return new ArmorStandEntityHelper(armorStand);
        } else if (base instanceof EndCrystalEntity crystal) {
            return new EndCrystalEntityHelper(crystal);
        } else if (base instanceof ItemFrameEntity itemFrame) {
            return new ItemFrameEntityHelper(itemFrame);
        } else if (base instanceof PaintingEntity painting) {
            return new PaintingEntityHelper(painting);
        }

        if (base instanceof BoatEntity boat) {
            return new BoatEntityHelper(boat);
        } else if (base instanceof FurnaceMinecartEntity furnaceMinecart) {
            return new FurnaceMinecartEntityHelper(furnaceMinecart);
        } else if (base instanceof TntMinecartEntity tntMinecart) {
            return new TntMinecartEntityHelper(tntMinecart);
        }

        return this;
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
