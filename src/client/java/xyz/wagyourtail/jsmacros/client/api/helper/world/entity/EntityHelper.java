package xyz.wagyourtail.jsmacros.client.api.helper.world.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.jsmacros.api.math.Pos2D;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockDataHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.ChunkHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.DirectionHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.boss.EnderDragonEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.boss.WitherEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration.ArmorStandEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration.EndCrystalEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration.ItemFrameEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration.PaintingEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display.BlockDisplayEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display.DisplayEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display.ItemDisplayEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display.TextDisplayEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob.*;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.other.InteractionEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive.*;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile.ArrowEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile.FishingBobberEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile.TridentEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile.WitherSkullEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.vehicle.BoatEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.vehicle.FurnaceMinecartEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.vehicle.TntMinecartEntityHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static xyz.wagyourtail.jsmacros.client.api.classes.render.components.RenderElement.mc;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class EntityHelper<T extends Entity> extends BaseHelper<T> {

    protected EntityHelper(T e) {
        super(e);
    }

    /**
     * @return entity position.
     */
    public Pos3D getPos() {
        return new Pos3D(base.getX(), base.getY(), base.getZ());
    }

    /**
     * @return entity block position.
     * @since 1.6.5
     */
    public BlockPosHelper getBlockPos() {
        return new BlockPosHelper(base.getBlockPos());
    }

    /**
     * @return the entity's eye position.
     * @since 1.8.4
     */
    public Pos3D getEyePos() {
        return new Pos3D(base.getEyePos());
    }

    /**
     * @return entity chunk coordinates. Since Pos2D only has x and y fields, z coord is y.
     * @since 1.6.5
     */
    public Pos2D getChunkPos() {
        return new Pos2D(base.getChunkPos().x, base.getChunkPos().z);
    }

    /**
     * @return the {@code x} value of the entity.
     * @since 1.0.8
     */
    public double getX() {
        return base.getX();
    }

    /**
     * @return the {@code y} value of the entity.
     * @since 1.0.8
     */
    public double getY() {
        return base.getY();
    }

    /**
     * @return the {@code z} value of the entity.
     * @since 1.0.8
     */
    public double getZ() {
        return base.getZ();
    }

    /**
     * @return the current eye height offset for the entity.
     * @since 1.2.8
     */
    public double getEyeHeight() {
        return base.getEyeHeight(base.getPose());
    }

    /**
     * @return the {@code pitch} value of the entity.
     * @since 1.0.8
     */
    public float getPitch() {
        return base.getPitch();
    }

    /**
     * @return the {@code yaw} value of the entity.
     * @since 1.0.8
     */
    public float getYaw() {
        return MathHelper.wrapDegrees(base.getYaw());
    }

    /**
     * @return the name of the entity.
     * @since 1.0.8 [citation needed], returned string until 1.6.4
     */
    public TextHelper getName() {
        return TextHelper.wrap(base.getName());
    }

    /**
     * @return the type of the entity.
     */
    @DocletReplaceReturn("EntityId")
    public String getType() {
        return EntityType.getId(base.getType()).toString();
    }

    /**
     * checks if this entity type equals to any of the specified types<br>
     * @since 1.9.0
     */
    @DocletReplaceTypeParams("E extends CanOmitNamespace<EntityId>")
    @DocletReplaceParams("...anyOf: JavaVarArgs<E>")
    @DocletReplaceReturn("this is EntityTypeFromId<E>")
    public boolean is(String ...types) {
        return Arrays.stream(types).map(RegistryHelper::parseNameSpace).anyMatch(getType()::equals);
    }

    /**
     * @return if the entity has the glowing effect.
     * @since 1.1.9
     */
    public boolean isGlowing() {
        return base.isGlowing();
    }

    /**
     * @return if the entity is in lava.
     * @since 1.1.9
     */
    public boolean isInLava() {
        return base.isInLava();
    }

    /**
     * @return if the entity is on fire.
     * @since 1.1.9
     */
    public boolean isOnFire() {
        return base.isOnFire();
    }

    /**
     * @return {@code true} if the entity is sneaking, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSneaking() {
        return base.isSneaking();
    }

    /**
     * @return {@code true} if the entity is sprinting, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSprinting() {
        return base.isSprinting();
    }

    /**
     * @return the vehicle of the entity.
     * @since 1.1.8 [citation needed]
     */
    @Nullable
    public EntityHelper<?> getVehicle() {
        Entity parent = base.getVehicle();
        if (parent != null) {
            return EntityHelper.create(parent);
        }
        return null;
    }

    /**
     * @since 1.9.0
     */
    @Nullable
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        BlockHitResult h = (BlockHitResult) base.raycast(distance, 0, fluid);
        if (h.getType() == HitResult.Type.MISS) {
            return null;
        }
        BlockState b = base.getWorld().getBlockState(h.getBlockPos());
        BlockEntity t = base.getWorld().getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.VOID_AIR)) {
            return null;
        }
        return new BlockDataHelper(b, t, h.getBlockPos());
    }


    /**
     * @since 1.9.0
     * @param distance
     * @return
     */
    @Nullable
    public EntityHelper<?> rayTraceEntity(int distance) {
        return DebugRenderer.getTargetedEntity(base, distance).map(EntityHelper::create).orElse(null);
    }

    /**
     * @return the entity passengers.
     * @since 1.1.8 [citation needed]
     */
    @Nullable
    public List<EntityHelper<?>> getPassengers() {
        List<EntityHelper<?>> entities = base.getPassengerList().stream().map(EntityHelper::create).collect(Collectors.toList());
        return entities.size() == 0 ? null : entities;

    }

    /**
     * @return
     * @since 1.2.8, was a {@link String} until 1.5.0
     */
    public NBTElementHelper.NBTCompoundHelper getNBT() {
        NbtCompound nbt = new NbtCompound();
        WriteView view = NbtWriteView.create(
                ErrorReporter.EMPTY,
                Objects.requireNonNull(mc.getNetworkHandler()).getRegistryManager()
        );
        base.writeData(view);
        NbtCompound result = ((NbtWriteView) view).getNbt();
        return NBTElementHelper.wrapCompound(result);
    }

    /**
     * @param name
     * @since 1.6.4
     */
    public EntityHelper<T> setCustomName(@Nullable TextHelper name) {
        if (name == null) {
            base.setCustomName(null);
        } else {
            base.setCustomName(name.getRaw());
        }
        return this;
    }

    /**
     * sets the name to always display
     *
     * @param b
     * @since 1.8.0
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
     *
     * @return glow color
     * @since 1.8.2
     */
    public int getGlowingColor() {
        return base.getTeamColorValue();
    }

    /**
     * Sets whether the entity is glowing.
     *
     * @param val
     * @return
     * @since 1.1.9
     */
    public EntityHelper<T> setGlowing(boolean val) {
        ((IMixinEntity) base).jsmacros_setForceGlowing(val ? 2 : 0);
        return this;
    }

    /**
     * reset the glowing effect to proper value.
     *
     * @return
     * @since 1.6.3
     */
    public EntityHelper<T> resetGlowing() {
        ((IMixinEntity) base).jsmacros_setForceGlowing(1);
        return this;
    }

    /**
     * Checks if the entity is still alive.
     *
     * @return
     * @since 1.2.8
     */
    public boolean isAlive() {
        return base.isAlive();
    }

    /**
     * @return UUID of the entity, random* if not a player, otherwise the player's uuid.
     * @since 1.6.5
     */
    public String getUUID() {
        return base.getUuid().toString();
    }

    /**
     * @return the maximum amount of air this entity can have.
     * @since 1.8.4
     */
    public int getMaxAir() {
        return base.getMaxAir();
    }

    /**
     * @return the amount of air this entity has.
     * @since 1.8.4
     */
    public int getAir() {
        return base.getAir();
    }

    /**
     * @return this entity's current speed in blocks per second.
     * @since 1.8.4
     */
    public double getSpeed() {
        double dx = Math.abs(base.getX() - base.lastX);
        double dz = Math.abs(base.getZ() - base.lastZ);
        return Math.sqrt(dx * dx + dz * dz) * 20;
    }

    /**
     * @return the direction the entity is facing, rounded to the nearest 45 degrees.
     * @since 1.8.4
     */
    public DirectionHelper getFacingDirection() {
        return new DirectionHelper(base.getHorizontalFacing());
    }

    /**
     * @return the distance between this entity and the specified one.
     * @since 1.8.4
     */
    public float distanceTo(EntityHelper<?> entity) {
        return base.distanceTo(entity.getRaw());
    }

    /**
     * @return the distance between this entity and the specified position.
     * @since 1.8.4
     */
    public double distanceTo(BlockPosHelper pos) {
        return Math.sqrt(pos.getRaw().getSquaredDistance(base.getPos()));
    }

    /**
     * @return the distance between this entity and the specified position.
     * @since 1.8.4
     */
    public double distanceTo(Pos3D pos) {
        return Math.sqrt(base.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()));
    }

    /**
     * @return the distance between this entity and the specified position.
     * @since 1.8.4
     */
    public double distanceTo(double x, double y, double z) {
        return Math.sqrt(base.squaredDistanceTo(x, y, z));
    }

    /**
     * @return the velocity vector.
     * @since 1.8.4
     */
    public Pos3D getVelocity() {
        return new Pos3D(base.getVelocity().x, base.getVelocity().y, base.getVelocity().z);
    }

    /**
     * @return the chunk helper for the chunk this entity is in.
     * @since 1.8.4
     */
    public ChunkHelper getChunk() {
        return new ChunkHelper(base.getWorld().getChunk(base.getBlockPos()));
    }

    /**
     * @return the name of the biome this entity is in.
     * @since 1.8.4
     */
    @DocletReplaceReturn("Biome")
    public String getBiome() {
        return MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getId(MinecraftClient.getInstance().world.getBiome(base.getBlockPos()).value()).toString();
    }

    @Override
    public String toString() {
        return String.format("%s:{\"name\": \"%s\", \"type\": \"%s\"}", getClass().getSimpleName(), this.getName(), this.getType());
    }

    /**
     * mostly for internal use.
     *
     * @param e mc entity.
     * @return correct subclass of this.
     */
    public static EntityHelper<?> create(@NotNull Entity e) {
        Objects.requireNonNull(e, "Entity cannot be null.");

        // Players
        if (e instanceof ClientPlayerEntity) {
            return new ClientPlayerEntityHelper<>((ClientPlayerEntity) e);
        }
        if (e instanceof PlayerEntity) {
            return new PlayerEntityHelper<>((PlayerEntity) e);
        }

        if (e instanceof MobEntity) {
            // Merchants
            if (e instanceof VillagerEntity) {
                return new VillagerEntityHelper((VillagerEntity) e);
            }
            if (e instanceof MerchantEntity) {
                return new MerchantEntityHelper<>((MerchantEntity) e);
            }

            // Bosses
            if (e instanceof EnderDragonEntity) {
                return new EnderDragonEntityHelper(((EnderDragonEntity) e));
            } else if (e instanceof WitherEntity) {
                return new WitherEntityHelper(((WitherEntity) e));
            }

            // Hostile mobs
            if (e instanceof AbstractPiglinEntity) {
                if (e instanceof PiglinEntity) {
                    return new PiglinEntityHelper(((PiglinEntity) e));
                } else {
                    return new AbstractPiglinEntityHelper<>(((AbstractPiglinEntity) e));
                }
            } else if (e instanceof CreeperEntity) {
                return new CreeperEntityHelper(((CreeperEntity) e));
            } else if (e instanceof ZombieEntity) {
                if (e instanceof DrownedEntity) {
                    return new DrownedEntityHelper(((DrownedEntity) e));
                } else if (e instanceof ZombieVillagerEntity) {
                    return new ZombieVillagerEntityHelper(((ZombieVillagerEntity) e));
                } else {
                    return new ZombieEntityHelper<>(((ZombieEntity) e));
                }
            } else if (e instanceof EndermanEntity) {
                return new EndermanEntityHelper(((EndermanEntity) e));
            } else if (e instanceof GhastEntity) {
                return new GhastEntityHelper(((GhastEntity) e));
            } else if (e instanceof BlazeEntity) {
                return new BlazeEntityHelper(((BlazeEntity) e));
            } else if (e instanceof GuardianEntity) {
                return new GuardianEntityHelper(((GuardianEntity) e));
            } else if (e instanceof PhantomEntity) {
                return new PhantomEntityHelper(((PhantomEntity) e));
            } else if (e instanceof IllagerEntity) {
                if (e instanceof VindicatorEntity) {
                    return new VindicatorEntityHelper(((VindicatorEntity) e));
                } else if (e instanceof PillagerEntity) {
                    return new PillagerEntityHelper(((PillagerEntity) e));
                } else if (e instanceof SpellcastingIllagerEntity) {
                    return new SpellcastingIllagerEntityHelper<>(((SpellcastingIllagerEntity) e));
                } else {
                    return new IllagerEntityHelper<>(((IllagerEntity) e));
                }
            } else if (e instanceof ShulkerEntity) {
                return new ShulkerEntityHelper(((ShulkerEntity) e));
            } else if (e instanceof SlimeEntity) {
                return new SlimeEntityHelper(((SlimeEntity) e));
            } else if (e instanceof SpiderEntity) {
                return new SpiderEntityHelper(((SpiderEntity) e));
            } else if (e instanceof VexEntity) {
                return new VexEntityHelper(((VexEntity) e));
            } else if (e instanceof WardenEntity) {
                return new WardenEntityHelper(((WardenEntity) e));
            } else if (e instanceof WitchEntity) {
                return new WitchEntityHelper(((WitchEntity) e));
            }

            // Animals
            if (e instanceof AnimalEntity) {
                if (e instanceof AbstractHorseEntity) {
                    if (e instanceof HorseEntity) {
                        return new HorseEntityHelper(((HorseEntity) e));
                    } else if (e instanceof AbstractDonkeyEntity) {
                        if (e instanceof LlamaEntity) {
                            return new LlamaEntityHelper<>(((LlamaEntity) e));
                        } else {
                            return new DonkeyEntityHelper<>(((AbstractDonkeyEntity) e));
                        }
                    } else {
                        return new AbstractHorseEntityHelper<>(((AbstractHorseEntity) e));
                    }
                } else if (e instanceof AxolotlEntity) {
                    return new AxolotlEntityHelper(((AxolotlEntity) e));
                } else if (e instanceof BeeEntity) {
                    return new BeeEntityHelper(((BeeEntity) e));
                } else if (e instanceof FoxEntity) {
                    return new FoxEntityHelper(((FoxEntity) e));
                } else if (e instanceof FrogEntity) {
                    return new FrogEntityHelper(((FrogEntity) e));
                } else if (e instanceof GoatEntity) {
                    return new GoatEntityHelper(((GoatEntity) e));
                } else if (e instanceof MooshroomEntity) {
                    return new MooshroomEntityHelper(((MooshroomEntity) e));
                } else if (e instanceof OcelotEntity) {
                    return new OcelotEntityHelper(((OcelotEntity) e));
                } else if (e instanceof PandaEntity) {
                    return new PandaEntityHelper(((PandaEntity) e));
                } else if (e instanceof PigEntity) {
                    return new PigEntityHelper(((PigEntity) e));
                } else if (e instanceof PolarBearEntity) {
                    return new PolarBearEntityHelper(((PolarBearEntity) e));
                } else if (e instanceof RabbitEntity) {
                    return new RabbitEntityHelper(((RabbitEntity) e));
                } else if (e instanceof SheepEntity) {
                    return new SheepEntityHelper(((SheepEntity) e));
                } else if (e instanceof StriderEntity) {
                    return new StriderEntityHelper(((StriderEntity) e));
                } else if (e instanceof TameableEntity) {
                    if (e instanceof CatEntity) {
                        return new CatEntityHelper(((CatEntity) e));
                    } else if (e instanceof WolfEntity) {
                        return new WolfEntityHelper(((WolfEntity) e));
                    } else if (e instanceof ParrotEntity) {
                        return new ParrotEntityHelper(((ParrotEntity) e));
                    } else {
                        return new TameableEntityHelper<>(((TameableEntity) e));
                    }
                } else {
                    return new AnimalEntityHelper<>(((AnimalEntity) e));
                }
            }

            // Neutral mobs
            if (e instanceof AllayEntity) {
                return new AllayEntityHelper(((AllayEntity) e));
            } else if (e instanceof BatEntity) {
                return new BatEntityHelper(((BatEntity) e));
            } else if (e instanceof DolphinEntity) {
                return new DolphinEntityHelper(((DolphinEntity) e));
            } else if (e instanceof IronGolemEntity) {
                return new IronGolemEntityHelper(((IronGolemEntity) e));
            } else if (e instanceof SnowGolemEntity) {
                return new SnowGolemEntityHelper(((SnowGolemEntity) e));
            } else if (e instanceof FishEntity) {
                if (e instanceof PufferfishEntity) {
                    return new PufferfishEntityHelper(((PufferfishEntity) e));
                } else if (e instanceof TropicalFishEntity) {
                    return new TropicalFishEntityHelper(((TropicalFishEntity) e));
                } else {
                    return new FishEntityHelper<>(((FishEntity) e));
                }
            }
        }

        // Projectiles
        if (e instanceof ProjectileEntity) {
            if (e instanceof ArrowEntity) {
                return new ArrowEntityHelper(((ArrowEntity) e));
            } else if (e instanceof FishingBobberEntity) {
                return new FishingBobberEntityHelper(((FishingBobberEntity) e));
            } else if (e instanceof TridentEntity) {
                return new TridentEntityHelper(((TridentEntity) e));
            } else if (e instanceof WitherSkullEntity) {
                return new WitherSkullEntityHelper(((WitherSkullEntity) e));
            }
        }

        // Decorations
        if (e instanceof ArmorStandEntity) {
            return new ArmorStandEntityHelper(((ArmorStandEntity) e));
        } else if (e instanceof EndCrystalEntity) {
            return new EndCrystalEntityHelper(((EndCrystalEntity) e));
        } else if (e instanceof ItemFrameEntity) {
            return new ItemFrameEntityHelper(((ItemFrameEntity) e));
        } else if (e instanceof PaintingEntity) {
            return new PaintingEntityHelper(((PaintingEntity) e));
        }

        // Vehicles
        if (e instanceof BoatEntity) {
            return new BoatEntityHelper(((BoatEntity) e));
        } else if (e instanceof FurnaceMinecartEntity) {
            return new FurnaceMinecartEntityHelper(((FurnaceMinecartEntity) e));
        } else if (e instanceof TntMinecartEntity) {
            return new TntMinecartEntityHelper(((TntMinecartEntity) e));
        }

        if (e instanceof LivingEntity) {
            return new LivingEntityHelper<>((LivingEntity) e);
        }
        if (e instanceof ItemEntity) {
            return new ItemEntityHelper((ItemEntity) e);
        }
        if (e instanceof DisplayEntity) {
            if (e instanceof DisplayEntity.ItemDisplayEntity) {
                return new ItemDisplayEntityHelper((DisplayEntity.ItemDisplayEntity) e);
            } else if (e instanceof DisplayEntity.TextDisplayEntity) {
                return new TextDisplayEntityHelper((DisplayEntity.TextDisplayEntity) e);
            } else if (e instanceof DisplayEntity.BlockDisplayEntity) {
                return new BlockDisplayEntityHelper((DisplayEntity.BlockDisplayEntity) e);
            }
            return new DisplayEntityHelper<>((DisplayEntity) e);
        }
        if (e instanceof InteractionEntity) {
            return new InteractionEntityHelper((InteractionEntity) e);
        }
        return new EntityHelper<>(e);
    }

    /**
     * @return cast of this entity helper (mainly for typescript)
     * @since 1.6.3
     */
    public ClientPlayerEntityHelper<?> asClientPlayer() {
        return (ClientPlayerEntityHelper<?>) this;
    }

    /**
     * @return cast of this entity helper (mainly for typescript)
     * @since 1.6.3
     */
    public PlayerEntityHelper<?> asPlayer() {
        return (PlayerEntityHelper<?>) this;
    }

    /**
     * @return cast of this entity helper (mainly for typescript)
     * @since 1.6.3
     */
    public VillagerEntityHelper asVillager() {
        return (VillagerEntityHelper) this;
    }

    /**
     * @return cast of this entity helper (mainly for typescript)
     * @since 1.6.3
     */
    public MerchantEntityHelper<?> asMerchant() {
        return (MerchantEntityHelper<?>) this;
    }

    /**
     * @return cast of this entity helper (mainly for typescript)
     * @since 1.6.3
     */
    public LivingEntityHelper<?> asLiving() {
        return (LivingEntityHelper<?>) this;
    }

    /**
     * @return this helper as an animal entity helper (mainly for typescript).
     * @since 1.8.4
     */
    public LivingEntityHelper<?> asAnimal() {
        return (AnimalEntityHelper<?>) this;
    }

    /**
     * @return cast of this entity helper (mainly for typescript)
     * @since 1.6.3
     */
    public ItemEntityHelper asItem() {
        return (ItemEntityHelper) this;
    }

    /**
     * @return the entity as a server entity if an integrated server is running and {@code null} otherwise.
     * @since 1.8.4
     */
    @Nullable
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
