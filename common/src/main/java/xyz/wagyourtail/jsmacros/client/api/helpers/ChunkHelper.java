package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockDataHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ChunkHelper extends BaseHelper<Chunk> {

    public ChunkHelper(Chunk base) {
        super(base);
    }

    /**
     * @return the first block (0 0 0 coordinate) of this chunk.
     *
     * @since 1.8.4
     */
    public BlockPosHelper getStartingBlock() {
        return new BlockPosHelper(base.getPos().getBlockPos(0, 0, 0));
    }

    /**
     * The coordinates are relative to the starting chunk position, see
     * {@link #getStartingBlock()}.
     *
     * @param xOffset the xOffset offset
     * @param y       the actual y coordinate
     * @param zOffset the zOffset offset
     * @return the block offset from the starting block of this chunk by xOffset y zOffset.
     *
     * @since 1.8.4
     */
    public BlockPosHelper getOffsetBlock(int xOffset, int y, int zOffset) {
        return new BlockPosHelper(base.getPos().getBlockPos(xOffset, y, zOffset));
    }

    /**
     * @return the maximum {@code y} position of all blocks inside this chunk.
     *
     * @since 1.8.4
     */
    public int getTopY() {
        return base.getTopY();
    }

    /**
     * @param xOffset   the xOffset coordinate
     * @param zOffset   the zOffset coordinate
     * @param heightmap the heightmap to use
     * @return the maximum {@code y} position of all blocks inside this chunk.
     *
     * @since 1.8.4
     */
    public int getTopYAt(int xOffset, int zOffset, Heightmap heightmap) {
        return heightmap.get(xOffset, zOffset);
    }

    /**
     * @return the minimum {@code y} position of all blocks inside this chunk.
     *
     * @since 1.8.4
     */
    public int getBottomY() {
        return base.getBottomY();
    }

    /**
     * @return the {@code x} coordinate (not the world coordinate) of this chunk.
     *
     * @since 1.8.4
     */
    public int getChunkX() {
        return base.getPos().x;
    }

    /**
     * @return the {@code z} coordinate (not the world coordinate) of this chunk.
     *
     * @since 1.8.4
     */
    public int getChunkZ() {
        return base.getPos().z;
    }

    /**
     * @param xOffset the x offset
     * @param y       the y coordinate
     * @param zOffset the z offset
     * @return the biome at the given position.
     *
     * @since 1.8.4
     */
    public String getBiome(int xOffset, int y, int zOffset) {
        return MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).getId(MinecraftClient.getInstance().world.getBiome(base.getPos().getBlockPos(xOffset, y, zOffset)).value()).toString();
    }

    /**
     * @return all entities inside this chunk.
     *
     * @since 1.8.4
     */
    public List<? extends EntityHelper<?>> getEntities() {
        return StreamSupport.stream(MinecraftClient.getInstance().world.getEntities().spliterator(), false).
                filter(entity -> entity.getChunkPos().equals(base.getPos())).map(EntityHelper::create).toList();
    }

    /**
     * @return all tile entity positions inside this chunk.
     *
     * @since 1.8.4
     */
    public List<BlockPosHelper> getTileEntities() {
        return base.getBlockEntityPositions().stream().map(BlockPosHelper::new).toList();
    }

    /**
     * @param callback   the callback function
     * @param includeAir whether to include air blocks or not
     * @since 1.8.4
     */
    public void forEach(MethodWrapper<BlockDataHelper, ?, ?, ?> callback, boolean includeAir) {
        //Maybe adapt this to the WorldScanner way?
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = base.getBottomY(); y < base.getTopY(); y++) {
                    BlockPos pos = base.getPos().getBlockPos(x, y, z);
                    BlockState state = base.getBlockState(pos);
                    if (!includeAir && state.isAir()) {
                        continue;
                    }
                    callback.accept(new BlockDataHelper(state, base.getBlockEntity(pos), pos));
                }
            }
        }
    }

    /**
     * @return a map of the raw heightmap data.
     *
     * @since 1.8.4
     */
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightMaps() {
        return base.getHeightmaps();
    }

    /**
     * @return the raw surface world generation heightmap.
     *
     * @since 1.8.4
     */
    public Heightmap getSurfaceWG() {
        return base.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
    }

    /**
     * @return the raw surface heightmap.
     *
     * @since 1.8.4
     */
    public Heightmap getSurface() {
        return base.getHeightmap(Heightmap.Type.WORLD_SURFACE);
    }

    /**
     * @return the raw ocean floor world generation heightmap.
     *
     * @since 1.8.4
     */
    public Heightmap getOceanFloorWG() {
        return base.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
    }

    /**
     * @return the raw ocean floor heightmap.
     *
     * @since 1.8.4
     */
    public Heightmap getOceanFloor() {
        return base.getHeightmap(Heightmap.Type.OCEAN_FLOOR);
    }

    /**
     * @return the raw motion blocking heightmap.
     *
     * @since 1.8.4
     */
    public Heightmap getMotionBlocking() {
        return base.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
    }

    /**
     * @return the raw motion blocking heightmap without leaves.
     *
     * @since 1.8.4
     */
    public Heightmap getMotionBlockingNoLeaves() {
        return base.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
    }

}
