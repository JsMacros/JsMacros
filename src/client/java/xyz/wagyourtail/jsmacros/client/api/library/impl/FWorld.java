package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScanner;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScannerBuilder;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.*;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.BossBarHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.PlayerEntityHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Functions for getting and using world data.
 * <p>
 * An instance of this class is passed to scripts as the {@code World} variable.
 *
 * @author Wagyourtail
 */
@Library("World")
@SuppressWarnings("unused")
public class FWorld extends BaseLibrary {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Don't modify.
     */
    public static double serverInstantTPS = 20;
    /**
     * Don't modify.
     */
    public static double server1MAverageTPS = 20;
    /**
     * Don't modify.
     */
    public static double server5MAverageTPS = 20;
    /**
     * Don't modify.
     */
    public static double server15MAverageTPS = 20;

    public FWorld(Core<?, ?> runner) {
        super(runner);
    }

    /**
     * returns whether a world is currently loaded
     *
     * @return
     * @since 1.3.0
     */
    public boolean isWorldLoaded() {
        return mc.world != null;
    }

    /**
     * @return players within render distance.
     */
    @Nullable
    public List<PlayerEntityHelper<PlayerEntity>> getLoadedPlayers() {
        ClientWorld world = mc.world;
        if (world == null) return null;
        List<PlayerEntityHelper<PlayerEntity>> players = new ArrayList<>();
        for (AbstractClientPlayerEntity p : ImmutableList.copyOf(world.getPlayers())) {
            players.add(new PlayerEntityHelper<>(p));
        }
        return players;
    }

    /**
     * @return players on the tablist.
     */
    @Nullable
    public List<PlayerListEntryHelper> getPlayers() {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null) return null;
        List<PlayerListEntryHelper> players = new ArrayList<>();
        for (PlayerListEntry p : ImmutableList.copyOf(handler.getPlayerList())) {
            players.add(new PlayerListEntryHelper(p));
        }
        return players;
    }

    /**
     * @param name the name of the player to get the entry for
     * @return player entry for the given player's name or {@code null} if not found.
     * @since 1.8.4
     */
    @Nullable
    public PlayerListEntryHelper getPlayerEntry(String name) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null) return null;
        PlayerListEntry entry = handler.getPlayerListEntry(name);
        return entry != null ? new PlayerListEntryHelper(entry) : null;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return The block at that position.
     */
    @Nullable
    public BlockDataHelper getBlock(int x, int y, int z) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        BlockPos bp = new BlockPos(x, y, z);
        BlockState b = world.getBlockState(bp);
        BlockEntity t = world.getBlockEntity(bp);
        if (b.getBlock().equals(Blocks.VOID_AIR)) {
            return null;
        }
        return new BlockDataHelper(b, t, bp);
    }

    @Nullable
    public BlockDataHelper getBlock(Pos3D pos) {
        return getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
    }

    @Nullable
    public BlockDataHelper getBlock(BlockPosHelper pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * The x and z position of the chunk can be calculated by the following formula: xChunk =
     * x >> 4; zChunk = z >> 4;
     *
     * @param x the x coordinate of the chunk, not the absolute position
     * @param z the z coordinate of the chunk, not the absolute position
     * @return ChunkHelper for the chunk coordinates {@link ChunkHelper}.
     * @since 1.8.4
     */
    @Nullable
    public ChunkHelper getChunk(int x, int z) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        return new ChunkHelper(world.getChunk(x, z));
    }

    /**
     * Usage: <br>
     * This will return all blocks that are facing south, don't require a tool to break,
     * have a hardness of 10 or less and whose name contains either chest or barrel.
     * <pre>
     * World.getWorldScanner()
     *     .withBlockFilter("getHardness").is("<=", 10)
     *     .andStringBlockFilter().contains("chest", "barrel")
     *     .withStringStateFilter().contains("facing=south")
     *     .andStateFilter("isToolRequired").is(false)
     *     .build()
     * </pre>
     *
     * @return a builder to create a WorldScanner.
     * @since 1.6.5
     */
    @DocletReplaceReturn("TypedWorldScannerBuilder.Initial")
    public WorldScannerBuilder getWorldScanner() {
        return new WorldScannerBuilder();
    }

    /**
     * @return a scanner for the current world.
     * @since 1.6.5
     */
    @Nullable
    public WorldScanner getWorldScanner(@Nullable MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, @Nullable MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        return new WorldScanner(world, blockFilter, stateFilter);
    }

    /**
     * @param id
     * @param chunkrange
     * @return
     * @since 1.6.4
     */
    @Nullable
    @DocletReplaceParams("centerX: int, centerZ: int, id: CanOmitNamespace<BlockId>, chunkrange: int")
    public List<Pos3D> findBlocksMatching(int centerX, int centerZ, String id, int chunkrange) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        String finalId = RegistryHelper.parseNameSpace(id);
        return new WorldScanner(world, block -> Registries.BLOCK.getId(block.getRaw()).toString().equals(finalId), null).scanChunkRange(centerX, centerZ, chunkrange);
    }

    /**
     * @param id
     * @param chunkrange
     * @return
     * @since 1.6.4
     */
    @Nullable
    @DocletReplaceParams("id: CanOmitNamespace<BlockId>, chunkrange: int")
    public List<Pos3D> findBlocksMatching(String id, int chunkrange) {
        ClientWorld world = mc.world;
        ClientPlayerEntity player = mc.player;
        if (world == null || player == null) return null;
        String finalId = RegistryHelper.parseNameSpace(id);
        int playerChunkX = player.getBlockX() >> 4;
        int playerChunkZ = player.getBlockZ() >> 4;
        return new WorldScanner(world, block -> Registries.BLOCK.getId(block.getRaw()).toString().equals(finalId), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    /**
     * @param ids
     * @param chunkrange
     * @return
     * @since 1.6.4
     */
    @Nullable
    @DocletReplaceParams("ids: CanOmitNamespace<BlockId>[], chunkrange: int")
    public List<Pos3D> findBlocksMatching(String[] ids, int chunkrange) {
        ClientWorld world = mc.world;
        ClientPlayerEntity player = mc.player;
        if (world == null || player == null) return null;
        int playerChunkX = player.getBlockX() >> 4;
        int playerChunkZ = player.getBlockZ() >> 4;
        Set<String> ids2 = Arrays.stream(ids).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        return new WorldScanner(world, block -> ids2.contains(Registries.BLOCK.getId(block.getRaw()).toString()), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    /**
     * @param centerX
     * @param centerZ
     * @param ids
     * @param chunkrange
     * @return
     * @since 1.6.4
     */
    @Nullable
    @DocletReplaceParams("centerX: int, centerZ: int, ids: CanOmitNamespace<BlockId>[], chunkrange: int")
    public List<Pos3D> findBlocksMatching(int centerX, int centerZ, String[] ids, int chunkrange) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        Set<String> ids2 = Arrays.stream(ids).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        return new WorldScanner(world, block -> ids2.contains(Registries.BLOCK.getId(block.getRaw()).toString()), null).scanChunkRange(centerX, centerZ, chunkrange);
    }

    /**
     * @param blockFilter
     * @param stateFilter
     * @param chunkrange
     * @return
     * @since 1.6.4
     */
    @Nullable
    public List<Pos3D> findBlocksMatching(MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, @Nullable MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) {
            throw new IllegalArgumentException("idFilter cannot be null");
        }
        ClientPlayerEntity player = mc.player;
        if (player == null) return null;
        int playerChunkX = player.getBlockX() >> 4;
        int playerChunkZ = player.getBlockZ() >> 4;
        return findBlocksMatching(playerChunkX, playerChunkZ, blockFilter, stateFilter, chunkrange);
    }

    /**
     * @param chunkX
     * @param chunkZ
     * @param blockFilter
     * @param stateFilter
     * @param chunkrange
     * @return
     * @since 1.6.4
     */
    @Nullable
    public List<Pos3D> findBlocksMatching(int chunkX, int chunkZ, MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, @Nullable MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) {
            throw new IllegalArgumentException("block filter cannot be null");
        }
        ClientWorld world = mc.world;
        if (world == null) return null;
        return new WorldScanner(world, blockFilter, stateFilter).scanChunkRange(chunkX, chunkZ, chunkrange);
    }

    /**
     * By default, air blocks are ignored and the callback is only called for real blocks.
     *
     * @param pos      the center position
     * @param radius   the radius to scan
     * @param callback the callback to call for each block
     * @since 1.8.4
     */
    public void iterateSphere(BlockPosHelper pos, int radius, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        iterateSphere(pos, radius, true, callback);
    }

    /**
     * @param pos       the center position
     * @param radius    the radius to scan
     * @param ignoreAir whether to ignore air blocks
     * @param callback  the callback to call for each block
     * @since 1.8.4
     */
    public void iterateSphere(BlockPosHelper pos, int radius, boolean ignoreAir, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius cannot be negative");
        }
        ClientWorld world = mc.world;
        if (world == null) return;
        int xStart = pos.getX() - radius;
        int yStart = MathHelper.clamp(world.getBottomY(), pos.getY() - radius, world.getHeight());
        int zStart = pos.getZ() - radius;
        int xEnd = pos.getX() + radius;
        int yEnd = MathHelper.clamp(world.getBottomY(), pos.getY() + radius, world.getHeight());
        int zEnd = pos.getZ() + radius;
        int radiusSq = radius * radius;

        BlockPos.Mutable blockPos = new BlockPos.Mutable();

        for (int x = xStart; x <= xEnd; x++) {
            int dx = x - pos.getX();
            for (int y = yStart; y <= yEnd; y++) {
                int dy = y - pos.getY();
                for (int z = zStart; z <= zEnd; z++) {
                    int dz = z - pos.getZ();
                    blockPos.set(x, y, z);
                    BlockState state = world.getBlockState(blockPos);
                    if (ignoreAir && state.isAir()) {
                        continue;
                    }
                    if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                        callback.accept(new BlockDataHelper(state, world.getBlockEntity(blockPos), blockPos));
                    }
                }
            }
        }
    }

    /**
     * @param pos1     the first position
     * @param pos2     the second position
     * @param callback the callback to call for each block
     * @since 1.8.4
     */
    public void iterateBox(BlockPosHelper pos1, BlockPosHelper pos2, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        iterateBox(pos1, pos2, true, callback);
    }

    /**
     * @param pos1      the first position
     * @param pos2      the second position
     * @param callback  the callback to call for each block
     * @param ignoreAir whether to ignore air blocks
     * @since 1.8.4
     */
    public void iterateBox(BlockPosHelper pos1, BlockPosHelper pos2, boolean ignoreAir, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        ClientWorld world = mc.world;
        if (world == null) return;
        BlockPos.stream(pos1.getRaw().withY(MathHelper.clamp(world.getBottomY(), pos1.getY(), world.getHeight())), pos2.getRaw().withY(MathHelper.clamp(world.getBottomY(), pos2.getY(), world.getHeight()))).forEach(bp -> {
            BlockState state = world.getBlockState(bp);
            if (ignoreAir && state.isAir()) {
                return;
            }
            callback.accept(new BlockDataHelper(state, world.getBlockEntity(bp), bp));
        });
    }

    /**
     * @return a helper for the scoreboards provided to the client.
     * @since 1.2.9
     */
    @Nullable
    public ScoreboardsHelper getScoreboards() {
        ClientWorld world = mc.world;
        if (world == null) return null;
        return new ScoreboardsHelper(world.getScoreboard());
    }

    /**
     * @return all entities in the render distance.
     */
    @Nullable
    public List<EntityHelper<?>> getEntities() {
        return getEntitiesInternal(entity -> true);
    }

    /**
     * @param types the entity types to consider
     * @return all entities in the render distance, that match the specified entity type.
     * @since 1.8.4
     */
    @Nullable
    @DocletReplaceTypeParams("E extends CanOmitNamespace<EntityId>")
    @DocletReplaceParams("...types: JavaVarArgs<E>")
    @DocletReplaceReturn("JavaList<EntityTypeFromId<E>> | null")
    public List<EntityHelper<?>> getEntities(String... types) {
        Set<String> uniqueTypes = Arrays.stream(types).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        Predicate<Entity> typePredicate = entity -> uniqueTypes.contains(Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        return getEntitiesInternal(typePredicate);
    }

    /**
     * @param distance the maximum distance to search for entities
     * @return a list of entities within the specified distance to the player.
     * @since 1.8.4
     */
    @Nullable
    public List<EntityHelper<?>> getEntities(double distance) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return null;
        Predicate<Entity> distancePredicate = e -> e.distanceTo(player) <= distance;
        return getEntitiesInternal(distancePredicate);
    }

    /**
     * @param distance the maximum distance to search for entities
     * @param types    the entity types to consider
     * @return a list of entities within the specified distance to the player, that match the specified entity type.
     * @since 1.8.4
     */
    @Nullable
    @DocletReplaceTypeParams("E extends CanOmitNamespace<EntityId>")
    @DocletReplaceParams("distance: double, ...types: JavaVarArgs<E>")
    @DocletReplaceReturn("JavaList<EntityTypeFromId<E>> | null")
    public List<EntityHelper<?>> getEntities(double distance, String... types) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return null;
        Set<String> uniqueTypes = Arrays.stream(types).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        Predicate<Entity> distancePredicate = e -> e.distanceTo(player) <= distance;
        Predicate<Entity> typePredicate = entity -> uniqueTypes.contains(Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        return getEntitiesInternal(distancePredicate.and(typePredicate));
    }

    /**
     * @param filter the entity filter
     * @return a list of entities that match the specified filter.
     * @since 1.8.4
     */
    @Nullable
    public List<EntityHelper<?>> getEntities(MethodWrapper<EntityHelper<?>, ?, ?, ?> filter) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        List<EntityHelper<?>> entities = new ArrayList<>();
        for (Entity e : tryCopyEntities(world, 0)) {
            EntityHelper<?> entity = EntityHelper.create(e);
            if (filter.test(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Nullable
    private List<EntityHelper<?>> getEntitiesInternal(Predicate<Entity> filter) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        List<EntityHelper<?>> entities = new ArrayList<>();
        for (Entity e : tryCopyEntities(world, 0)) {
            if (filter.test(e)) {
                entities.add(EntityHelper.create(e));
            }
        }
        return entities;
    }

    /**
     * @param tried always input 0 please. don't want to create an overload for private method.
     */
    private List<Entity> tryCopyEntities(ClientWorld world, int tried) {
        try {
            List<Entity> list = ImmutableList.copyOf(world.getEntities());
            if (tried != 0) {
                JsMacros.LOGGER.warn("FWorld.tryCopyEntities() did {} tries", tried + 1);
            }
            return list;
        } catch (NullPointerException e) {
            if (tried > 40) throw e; // give up, I wouldn't imagine that it exceeds 40 times
            return tryCopyEntities(world, tried + 1);
        }
    }

    /**
     * raytrace between two points returning the first block hit.
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param fluid
     * @return
     * @since 1.6.5
     */
    @Nullable
    public BlockDataHelper rayTraceBlock(double x1, double y1, double z1, double x2, double y2, double z2, boolean fluid) {
        ClientWorld world = mc.world;
        ClientPlayerEntity player = mc.player;
        if (world == null || player == null) return null;
        BlockHitResult result = world.raycast(new RaycastContext(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2), RaycastContext.ShapeType.COLLIDER, fluid ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, player));
        if (result.getType() != BlockHitResult.Type.MISS) {
            return new BlockDataHelper(world.getBlockState(result.getBlockPos()), world.getBlockEntity(result.getBlockPos()), result.getBlockPos());
        }
        return null;
    }

    /**
     * raytrace between two points returning the first entity hit.
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     * @since 1.8.3
     */
    @Nullable
    public EntityHelper<?> rayTraceEntity(double x1, double y1, double z1, double x2, double y2, double z2) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        TargetPredicate target = TargetPredicate.createNonAttackable();
        target.setPredicate((e, w) -> e.getBoundingBox().raycast(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2)).isPresent());
        List<LivingEntity> entities = (List) StreamSupport.stream(world.getEntities().spliterator(), false).filter(e -> e instanceof LivingEntity).collect(Collectors.toList());
        LivingEntity closest = null;
        double distance = -1;
        PlayerEntity tester = mc.player;
        for (LivingEntity e : entities) {
            if (target.test(null, tester, e)) {
                double d = e.squaredDistanceTo(tester);
                if (distance == -1 || d < distance) {
                    closest = e;
                    distance = d;
                }
            }
        }
        if (closest != null) {
            return EntityHelper.create(closest);
        }
        return null;
    }

    /**
     * note that some server might utilize dimension identifiers for mods to distinguish between worlds.
     * @return the current dimension.
     * @since 1.1.2
     */
    @Nullable
    @DocletReplaceReturn("Dimension | null")
    public String getDimension() {
        ClientWorld world = mc.world;
        if (world == null) return null;
        return world.getRegistryKey().getValue().toString();
    }

    /**
     * @return the current biome.
     * @since 1.1.5
     */
    @Nullable
    @DocletReplaceReturn("Biome | null")
    public String getBiome() {
        ClientWorld world = mc.world;
        ClientPlayerEntity player = mc.player;
        if (world == null || player == null) return null;
        Identifier id = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getId(world.getBiome(player.getBlockPos()).value());
        return id == null ? null : id.toString();
    }

    /**
     * ticks processed since world was started.
     *
     * @return the current world time. {@code -1} if world is not loaded.
     * @since 1.1.5
     */
    public long getTime() {
        ClientWorld world = mc.world;
        if (world == null) return -1;
        return world.getTime();
    }

    /**
     * ticks passed since world was started INCLUDING those skipped when nights were cut short with sleeping.
     *
     * @return the current world time of day. {@code -1} if world is not loaded.
     * @since 1.1.5
     */
    public long getTimeOfDay() {
        ClientWorld world = mc.world;
        if (world == null) return -1;
        return world.getTimeOfDay();
    }

    /**
     * @return {@code true} if it is daytime, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDay() {
        ClientWorld world = mc.world;
        if (world == null) return false;
        return world.isDay();
    }

    /**
     * @return {@code true} if it is nighttime, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isNight() {
        ClientWorld world = mc.world;
        if (world == null) return false;
        return world.isNight();
    }

    /**
     * @return {@code true} if it is raining, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isRaining() {
        ClientWorld world = mc.world;
        if (world == null) return false;
        return world.isRaining();
    }

    /**
     * @return {@code true} if it is thundering, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isThundering() {
        ClientWorld world = mc.world;
        if (world == null) return false;
        return world.isThundering();
    }

    /**
     * @return an identifier for the loaded world that is based on the world's name or server ip and
     * thus most likely unique enough to identify a specific world, or
     * {@code "UNKNOWN_NAME"} if no world was found.
     * @since 1.8.4
     */
    public String getWorldIdentifier() {
        IntegratedServer server = mc.getServer();
        if (server != null) {
            return "LOCAL_" + server.getSavePath(WorldSavePath.ROOT).normalize().getFileName();
        }
        ServerInfo multiplayerServer = mc.getCurrentServerEntry();
        if (multiplayerServer != null) {
            if (multiplayerServer.isRealm()) {
                return "REALM_" + multiplayerServer.name;
            }
            if (multiplayerServer.isLocal()) {
                return "LAN_" + multiplayerServer.name;
            }
            return multiplayerServer.address.replace(":25565", "").replace(":", "_");
        }
        return "UNKNOWN_NAME";
    }

    /**
     * @return respawn position.
     * @since 1.2.6
     */
    @Nullable
    public BlockPosHelper getRespawnPos() {
        ClientWorld world = mc.world;
        if (world == null) return null;
        return new BlockPosHelper(world.getSpawnPos());
    }

    /**
     * @return world difficulty as an {@link Integer Integer}. {@code -1} if world is not loaded.
     * @since 1.2.6
     */
    public int getDifficulty() {
        ClientWorld world = mc.world;
        if (world == null) return -1;
        return world.getDifficulty().getId();
    }

    /**
     * @return moon phase as an {@link Integer Integer}. {@code -1} if world is not loaded.
     * @since 1.2.6
     */
    public int getMoonPhase() {
        ClientWorld world = mc.world;
        if (world == null) return -1;
        return world.getMoonPhase();
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return sky light as an {@link Integer Integer}. {@code -1} if world is not loaded.
     * @since 1.1.2
     */
    public int getSkyLight(int x, int y, int z) {
        ClientWorld world = mc.world;
        if (world == null) return -1;
        return world.getLightLevel(LightType.SKY, new BlockPos(x, y, z));
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return block light as an {@link Integer Integer}. {@code -1} if world is not loaded.
     * @since 1.1.2
     */
    public int getBlockLight(int x, int y, int z) {
        ClientWorld world = mc.world;
        if (world == null) return -1;
        return world.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
    }

    /**
     * plays a sound file using javax's sound stuff.
     *
     * @param file
     * @param volume
     * @return
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @since 1.1.7
     */
    public Clip playSoundFile(String file, double volume) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(JsMacrosClient.clientCore.config.macroFolder, file)));
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        double min = gainControl.getMinimum();
        double range = gainControl.getMaximum() - min;
        float gain = (float) ((range * volume) + min);
        gainControl.setValue(gain);
        clip.addLineListener(event -> {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                clip.close();
            }
        });
        clip.start();
        return clip;
    }

    /**
     * @param id
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @since 1.1.7
     */
    @DocletReplaceParams("id: SoundId")
    public void playSound(String id) {
        playSound(id, 1F);
    }

    /**
     * @param id
     * @param volume
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @since 1.1.7
     */
    @DocletReplaceParams("id: SoundId, volume: double")
    public void playSound(String id, double volume) {
        playSound(id, volume, 0.25F);
    }

    /**
     * @param id
     * @param volume
     * @param pitch
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @since 1.1.7
     */
    @DocletReplaceParams("id: CanOmitNamespace<SoundId>, volume: double, pitch: double")
    public void playSound(String id, double volume, double pitch) {
        SoundEvent sound = SoundEvent.of(Identifier.of(id));
        assert sound != null;
        mc.execute(() -> mc.getSoundManager().play(PositionedSoundInstance.master(sound, (float) pitch, (float) volume)));
    }

    /**
     * plays a minecraft sound using the internal system.
     *
     * @param id
     * @param volume
     * @param pitch
     * @param x
     * @param y
     * @param z
     * @since 1.1.7
     */
    @DocletReplaceParams("id: CanOmitNamespace<SoundId>, volume: double, pitch: double, x: double, y: double, z: double")
    public void playSound(String id, double volume, double pitch, double x, double y, double z) {
        ClientWorld world = mc.world;
        if (world == null) return;
        SoundEvent sound = SoundEvent.of(Identifier.of(id));
        assert sound != null;
        mc.execute(() -> world.playSoundClient(x, y, z, sound, SoundCategory.MASTER, (float) volume, (float) pitch, true));
    }

    /**
     * @return a map of boss bars by the boss bar's UUID.
     * @since 1.2.1
     */
    public Map<String, BossBarHelper> getBossBars() {
        assert mc.inGameHud != null;
        Map<UUID, ClientBossBar> bars = ImmutableMap.copyOf(mc.inGameHud.getBossBarHud().bossBars);
        Map<String, BossBarHelper> out = new HashMap<>();
        for (Map.Entry<UUID, ClientBossBar> e : ImmutableList.copyOf(bars.entrySet())) {
            out.put(e.getKey().toString(), new BossBarHelper(e.getValue()));
        }
        return out;
    }

    /**
     * Check whether a chunk is within the render distance and loaded.
     *
     * @param chunkX
     * @param chunkZ
     * @return
     * @since 1.2.2
     */
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        ClientWorld world = mc.world;
        if (world == null) return false;
        return world.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }

    /**
     * @return the current server address as a string ({@code server.address/server.ip:port}).
     * @since 1.2.2
     */
    @Nullable
    public String getCurrentServerAddress() {
        ClientPlayNetworkHandler h = mc.getNetworkHandler();
        if (h == null) {
            return null;
        }
        ClientConnection c = h.getConnection();
        if (c == null) {
            return null;
        }
        return c.getAddress().toString();
    }

    /**
     * @param x
     * @param z
     * @return biome at specified location, only works if the block/chunk is loaded.
     * @since 1.2.2 [Citation Needed]
     */
    @Nullable
    @DocletReplaceReturn("Biome | null")
    public String getBiomeAt(int x, int z) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        Identifier id = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getId(world.getBiome(new BlockPos(x, 10, z)).value());
        return id == null ? null : id.toString();
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return biome at specified location, only works if the block/chunk is loaded.
     * @since 1.8.4
     */
    @Nullable
    @DocletReplaceReturn("Biome | null")
    public String getBiomeAt(int x, int y, int z) {
        ClientWorld world = mc.world;
        if (world == null) return null;
        Identifier id = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getId(world.getBiome(new BlockPos(x, y, z)).value());
        return id == null ? null : id.toString();
    }

    /**
     * @return best attempt to measure and give the server tps with various timings.
     * @since 1.2.7
     */
    @DocletReplaceReturn("`${number}, 1M: ${number}, 5M: ${number}, 15M: ${number}`")
    public String getServerTPS() {
        return String.format("%.2f, 1M: %.1f, 5M: %.1f, 15M: %.1f", serverInstantTPS, server1MAverageTPS, server5MAverageTPS, server15MAverageTPS);
    }

    /**
     * @return text helper for the top part of the tab list (above the players)
     * @since 1.3.1
     */
    @Nullable
    public TextHelper getTabListHeader() {
        return TextHelper.wrap(((IPlayerListHud) mc.inGameHud.getPlayerListHud()).jsmacros_getHeader());
    }

    /**
     * @return text helper for the bottom part of the tab list (below the players)
     * @since 1.3.1
     */
    @Nullable
    public TextHelper getTabListFooter() {
        return TextHelper.wrap(((IPlayerListHud) mc.inGameHud.getPlayerListHud()).jsmacros_getFooter());
    }

    /**
     * Summons the amount of particles at the desired position.
     *
     * @param id    the particle id
     * @param x     the x position to spawn the particle
     * @param y     the y position to spawn the particle
     * @param z     the z position to spawn the particle
     * @param count the amount of particles to spawn
     * @since 1.8.4
     */
    @DocletReplaceParams("id: ParticleId, x: double, y: double, z: double, count: int")
    public void spawnParticle(String id, double x, double y, double z, int count) {
        spawnParticle(id, x, y, z, 0.1, 0.1, 0.1, 1, count, true);
    }

    /**
     * Summons the amount of particles at the desired position with some variation of delta and the
     * given speed.
     *
     * @param id     the particle id
     * @param x      the x position to spawn the particle
     * @param y      the y position to spawn the particle
     * @param z      the z position to spawn the particle
     * @param deltaX the x variation of the particle
     * @param deltaY the y variation of the particle
     * @param deltaZ the z variation of the particle
     * @param speed  the speed of the particle
     * @param count  the number of particles to spawn
     * @param force  whether to show the particle if it's more than 32 blocks away
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<ParticleId>, x: double, y: double, z: double, deltaX: double, deltaY: double, deltaZ: double, speed: double, count: int, force: boolean")
    public void spawnParticle(String id, double x, double y, double z, double deltaX, double deltaY, double deltaZ, double speed, int count, boolean force) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        ParticleEffect particle = (ParticleEffect) Registries.PARTICLE_TYPE.get(RegistryHelper.parseIdentifier(id));
        particle = particle != null ? particle : ParticleTypes.CLOUD;

        ParticleS2CPacket packet = new ParticleS2CPacket(particle, force, true, x, y, z, (float) deltaX, (float) deltaY, (float) deltaZ, (float) speed, count);
        mc.execute(() -> player.networkHandler.onParticle(packet));
    }

    /**
     * @return the raw minecraft world.
     * @since 1.9.1
     */
    @Nullable
    public ClientWorld getRaw() {
        return mc.world;
    }

    /**
     * @return best attempt to measure and give the server tps.
     * @since 1.2.7
     */
    public double getServerInstantTPS() {
        return serverInstantTPS;
    }

    /**
     * @return best attempt to measure and give the server tps over the previous 1 minute average.
     * @since 1.2.7
     */
    public double getServer1MAverageTPS() {
        return server1MAverageTPS;
    }

    /**
     * @return best attempt to measure and give the server tps over the previous 5 minute average.
     * @since 1.2.7
     */
    public double getServer5MAverageTPS() {
        return server5MAverageTPS;
    }

    /**
     * @return best attempt to measure and give the server tps over the previous 15 minute average.
     * @since 1.2.7
     */
    public double getServer15MAverageTPS() {
        return server15MAverageTPS;
    }

}
