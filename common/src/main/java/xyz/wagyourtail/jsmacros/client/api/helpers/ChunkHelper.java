package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
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
 * @since 1.9.0
 */
public class ChunkHelper extends BaseHelper<Chunk> {

    public ChunkHelper(Chunk base) {
        super(base);
    }

    /**
     * @return the first block (0 0 0 chunk coordinate) of the chunk
     *
     * @since 1.9.0
     */
    public BlockPosHelper getStartingBlock() {
        return new BlockPosHelper(base.getPos().getBlockPos(0, 0, 0));
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return the block offset from the starting block of the chunk by x y z
     *
     * @since 1.9.0
     */
    public BlockPosHelper getOffsetBlock(int x, int y, int z) {
        return new BlockPosHelper(base.getPos().getBlockPos(x, y, z));
    }

    /**
     * @return the max {@code y} position of all blocks inside the chunk
     *
     * @since 1.9.0
     */
    public int getTopY() {
        return base.getTopY();
    }

    /**
     * @return the min {@code y} position of all blocks inside the chunk
     *
     * @since 1.9.0
     */
    public int getBottomY() {
        return base.getBottomY();
    }

    /**
     * @return the {@code x} coordinate (not the world coordinate) of the chunk
     *
     * @since 1.9.0
     */
    public int getChunkX() {
        return base.getPos().x;
    }

    /**
     * @return the {@code z} coordinate (not the world coordinate) of the chunk
     *
     * @since 1.9.0
     */
    public int getChunkZ() {
        return base.getPos().z;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<? extends EntityHelper<?>> getEntities() {
        return StreamSupport.stream(MinecraftClient.getInstance().world.getEntities().spliterator(), false).
                filter(entity -> entity.getChunkPos() == base.getPos()).map(EntityHelper::create).toList();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public List<BlockPosHelper> getTileEntities() {
        return base.getBlockEntityPositions().stream().map(BlockPosHelper::new).toList();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightMaps() {
        return base.getHeightmaps();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public void forEach(boolean includeAir, MethodWrapper<BlockDataHelper, ?, ?, ?> consumer) {

    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Heightmap getSurfaceWG() {
        return base.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Heightmap getSurface() {
        return base.getHeightmap(Heightmap.Type.WORLD_SURFACE);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Heightmap getOceanFloorWG() {
        return base.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Heightmap getOceanFloor() {
        return base.getHeightmap(Heightmap.Type.OCEAN_FLOOR);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Heightmap getMotionBlocking() {
        return base.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public Heightmap getMotionBlockingNoLeaves() {
        return base.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
    }

}
