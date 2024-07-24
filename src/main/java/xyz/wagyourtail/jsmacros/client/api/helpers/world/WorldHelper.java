package xyz.wagyourtail.jsmacros.client.api.helpers.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScanner;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScannerBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @since 2.0.0
 */
public class WorldHelper extends BaseHelper<World> {

    public WorldHelper(World base) {
        super(base);
    }

    /**
     * note that some server might utilize dimension identifiers for mods to distinguish between worlds.
     * @return the current dimension.
     */
    @DocletReplaceReturn("Dimension")
    public String getDimension() {
        return base.getRegistryKey().getValue().toString();
    }

    /**
     * @return The block at that position.
     */
    @Nullable
    public BlockDataHelper getBlock(int x, int y, int z) {
        BlockPos bp = new BlockPos(x, y, z);
        BlockState b = base.getBlockState(bp);
        BlockEntity t = base.getBlockEntity(bp);
        if (b.getBlock().equals(Blocks.VOID_AIR)) {
            return null;
        }
        return new BlockDataHelper(b, t, bp);
    }

    /**
     *
     */
    @Nullable
    public BlockDataHelper getBlock(Pos3D pos) {
        return getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
    }

    /**
     *
     */
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
     */
    public ChunkHelper getChunk(int x, int z) {
        return new ChunkHelper(base.getChunk(x, z));
    }

    @DocletReplaceReturn("TypedWorldScannerBuilder.Initial")
    public WorldScannerBuilder getWorldScanner() {
        return new WorldScannerBuilder(base);
    }

    /**
     *
     */
    @DocletReplaceParams("centerX: int, centerZ: int, id: CanOmitNamespace<BlockId>, chunkrange: int")
    public List<Pos3D> findBlocksMatching(int centerX, int centerZ, String id, int chunkrange) {
        String finalId = RegistryHelper.parseNameSpace(id);
        return new WorldScanner(base, block -> Registries.BLOCK.getId(block.getRaw()).toString().equals(finalId), null).scanChunkRange(centerX, centerZ, chunkrange);
    }

    /**
     *
     */
    @Nullable
    @DocletReplaceParams("id: CanOmitNamespace<BlockId>, chunkrange: int")
    public List<Pos3D> findBlocksMatching(BlockPosHelper center, String id, int chunkrange) {
        String finalId = RegistryHelper.parseNameSpace(id);
        int playerChunkX = center.getX() >> 4;
        int playerChunkZ = center.getZ() >> 4;
        return new WorldScanner(base, block -> Registries.BLOCK.getId(block.getRaw()).toString().equals(finalId), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    /**
     *
     */
    @DocletReplaceParams("ids: CanOmitNamespace<BlockId>[], chunkrange: int")
    public List<Pos3D> findBlocksMatching(BlockPosHelper center, String[] ids, int chunkrange) {
        int playerChunkX = center.getX() >> 4;
        int playerChunkZ = center.getZ() >> 4;
        Set<String> ids2 = Arrays.stream(ids).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        return new WorldScanner(base, block -> ids2.contains(Registries.BLOCK.getId(block.getRaw()).toString()), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    @DocletReplaceParams("centerX: int, centerZ: int, ids: CanOmitNamespace<BlockId>[], chunkrange: int")
    public List<Pos3D> findBlocksMatching(int centerX, int centerZ, String[] ids, int chunkrange) {
        Set<String> ids2 = Arrays.stream(ids).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        return new WorldScanner(base, block -> ids2.contains(Registries.BLOCK.getId(block.getRaw()).toString()), null).scanChunkRange(centerX, centerZ, chunkrange);
    }


    public List<Pos3D> findBlocksMatching(BlockPosHelper center, MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, @Nullable MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) {
            throw new IllegalArgumentException("idFilter cannot be null");
        }
        int playerChunkX = center.getX() >> 4;
        int playerChunkZ = center.getZ() >> 4;
        return findBlocksMatching(playerChunkX, playerChunkZ, blockFilter, stateFilter, chunkrange);
    }

    @DocletReplaceParams("centerX: int, centerZ: int, id: CanOmitNamespace<BlockId>, chunkrange: int")
    public List<Pos3D> findBlocksMatching(int chunkX, int chunkZ, MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, @Nullable MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) {
            throw new IllegalArgumentException("block filter cannot be null");
        }
        return new WorldScanner(base, blockFilter, stateFilter).scanChunkRange(chunkX, chunkZ, chunkrange);
    }

    public void iterateSphere(BlockPosHelper pos, int radius, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        iterateSphere(pos, radius, true, callback);
    }

    public void iterateSphere(BlockPosHelper pos, int radius, boolean ignoreAir, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius cannot be negative");
        }
        int xStart = pos.getX() - radius;
        int yStart = MathHelper.clamp(base.getBottomY(), pos.getY() - radius, base.getHeight());
        int zStart = pos.getZ() - radius;
        int xEnd = pos.getX() + radius;
        int yEnd = MathHelper.clamp(base.getBottomY(), pos.getY() + radius, base.getHeight());
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
                    BlockState state = base.getBlockState(blockPos);
                    if (ignoreAir && state.isAir()) {
                        continue;
                    }
                    if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                        callback.accept(new BlockDataHelper(state, base.getBlockEntity(blockPos), blockPos));
                    }
                }
            }
        }
    }

    public void iterateBox(BlockPosHelper pos1, BlockPosHelper pos2, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        iterateBox(pos1, pos2, true, callback);
    }

    public void iterateBox(BlockPosHelper pos1, BlockPosHelper pos2, boolean ignoreAir, MethodWrapper<BlockDataHelper, ?, ?, ?> callback) {
        BlockPos.stream(pos1.getRaw().withY(MathHelper.clamp(base.getBottomY(), pos1.getY(), base.getHeight())), pos2.getRaw().withY(MathHelper.clamp(base.getBottomY(), pos2.getY(), base.getHeight()))).forEach(bp -> {
            BlockState state = base.getBlockState(bp);
            if (ignoreAir && state.isAir()) {
                return;
            }
            callback.accept(new BlockDataHelper(state, base.getBlockEntity(bp), bp));
        });
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
     */
    @DocletReplaceTypeParams("E extends CanOmitNamespace<EntityId>")
    @DocletReplaceParams("...types: E[]")
    @DocletReplaceReturn("JavaList<EntityTypeFromId<E>> | null")
    public List<EntityHelper<?>> getEntities(String... types) {
        Set<String> uniqueTypes = Arrays.stream(types).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        Predicate<Entity> typePredicate = entity -> uniqueTypes.contains(Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        return getEntitiesInternal(typePredicate);
    }

    /**
     * @param distance the maximum distance to search for entities
     * @return a list of entities within the specified distance to the player.
     */
    public List<EntityHelper<?>> getEntities(Pos3D center, double distance) {
        Vec3d centerVec = center.toMojangDoubleVector();
        double distanceSq = distance * distance;
        Predicate<Entity> distancePredicate = e -> e.squaredDistanceTo(centerVec) <= distanceSq;
        return getEntitiesInternal(distancePredicate);
    }

    /**
     * @param distance the maximum distance to search for entities
     * @param types    the entity types to consider
     * @return a list of entities within the specified distance to the player, that match the specified entity type.
     */
    @DocletReplaceTypeParams("E extends CanOmitNamespace<EntityId>")
    @DocletReplaceParams("distance: double, ...types: E[]")
    @DocletReplaceReturn("JavaList<EntityTypeFromId<E>> | null")
    public List<EntityHelper<?>> getEntities(Pos3D center, double distance, String... types) {
        Vec3d centerVec = center.toMojangDoubleVector();
        double distanceSq = distance * distance;
        Set<String> uniqueTypes = Arrays.stream(types).map(RegistryHelper::parseNameSpace).collect(Collectors.toUnmodifiableSet());
        Predicate<Entity> distancePredicate = e -> e.squaredDistanceTo(centerVec) <= distanceSq;
        Predicate<Entity> typePredicate = entity -> uniqueTypes.contains(Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        return getEntitiesInternal(distancePredicate.and(typePredicate));
    }

    /**
     * @param filter the entity filter
     * @return a list of entities that match the specified filter.
     */
    public List<EntityHelper<?>> getEntities(MethodWrapper<EntityHelper<?>, ?, ?, ?> filter) {
        List<EntityHelper<?>> entities = new ArrayList<>();
        for (Entity e : ImmutableList.copyOf(base.getEntityLookup().iterate())) {
            EntityHelper<?> entity = EntityHelper.create(e);
            if (filter.test(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    private List<EntityHelper<?>> getEntitiesInternal(Predicate<Entity> filter) {
        List<EntityHelper<?>> entities = new ArrayList<>();
        for (Entity e : ImmutableList.copyOf(base.getEntityLookup().iterate())) {
            if (filter.test(e)) {
                entities.add(EntityHelper.create(e));
            }
        }
        return entities;
    }

    public long getTime() {
        return base.getTime();
    }

    public long getTimeOfDay() {
        return base.getTimeOfDay();
    }

    public boolean isDay() {
        return base.isDay();
    }

    public boolean isNight() {
        return base.isNight();
    }

    public boolean isRaining() {
        return base.isRaining();
    }

    public boolean isThundering() {
        return base.isThundering();
    }

    public BlockPosHelper getRespawnPos() {
        return new BlockPosHelper(base.getSpawnPos());
    }

    public int getDifficulty() {
        return base.getDifficulty().getId();
    }

    public int getMoonPhase() {
        return base.getMoonPhase();
    }

    public int getSkyLight(int x, int y, int z) {
        return base.getLightLevel(LightType.SKY, new BlockPos(x, y, z));
    }

    public int getBlockLight(int x, int y, int z) {
        return base.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
    }

    @DocletReplaceReturn("Biome")
    public String getBiomeAt(int x, int z) {
        Identifier id = base.getRegistryManager().get(RegistryKeys.BIOME).getId(base.getBiome(new BlockPos(x, 10, z)).value());
        return id == null ? null : id.toString();
    }

    @DocletReplaceReturn("Biome")
    public String getBiomeAt(int x, int y, int z) {
        Identifier id = base.getRegistryManager().get(RegistryKeys.BIOME).getId(base.getBiome(new BlockPos(x, y, z)).value());
        return id == null ? null : id.toString();
    }

}
