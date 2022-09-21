package xyz.wagyourtail.jsmacros.client.api.helpers.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import xyz.wagyourtail.jsmacros.client.api.helpers.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.6
 */
@SuppressWarnings("unused")
public class BlockPosHelper extends BaseHelper<BlockPos> {
    
    public BlockPosHelper(BlockPos b) {
        super(b);
    }

    public BlockPosHelper(int x, int y, int z) {
        super(new BlockPos(x, y, z));
    }
    
    /**
     * @since 1.2.6
     * @return the {@code x} value of the block.
     */
    public int getX() {
        return base.getX();
    }
    
    /**
     * @since 1.2.6
     * @return the {@code y} value of the block.
     */
    public int getY() {
        return base.getY();
    }
    
    /**
     * @since 1.2.6
     * @return the {@code z} value of the block.
     */
    public int getZ() {
        return base.getZ();
    }

    /**
     * @return the block above.
     *
     * @since 1.6.5
     */
    public BlockPosHelper up() {
        return new BlockPosHelper(getX(), getY() + 1, getZ());
    }

    /**
     * @param distance the distance to move up
     * @return the block n-th block above.
     *
     * @since 1.6.5
     */
    public BlockPosHelper up(int distance) {
        return new BlockPosHelper(getX(), getY() + distance, getZ());
    }

    /**
     * @return the block below.
     *
     * @since 1.6.5
     */
    public BlockPosHelper down() {
        return new BlockPosHelper(getX(), getY() - 1, getZ());
    }

    /**
     * @param distance the distance to move down
     * @return the block n-th block below.
     *
     * @since 1.6.5
     */
    public BlockPosHelper down(int distance) {
        return new BlockPosHelper(getX(), getY() - distance, getZ());
    }

    /**
     * @return the block to the north.
     *
     * @since 1.6.5
     */
    public BlockPosHelper north() {
        return new BlockPosHelper(getX(), getY(), getZ() - 1);
    }

    /**
     * @param distance the distance to move north
     * @return the n-th block to the north.
     *
     * @since 1.6.5
     */
    public BlockPosHelper north(int distance) {
        return new BlockPosHelper(getX(), getY(), getZ() - distance);
    }

    /**
     * @return the block to the south.
     *
     * @since 1.6.5
     */
    public BlockPosHelper south() {
        return new BlockPosHelper(getX(), getY(), getZ() + 1);
    }

    /**
     * @param distance the distance to move south
     * @return the n-th block to the south.
     *
     * @since 1.6.5
     */
    public BlockPosHelper south(int distance) {
        return new BlockPosHelper(getX(), getY(), getZ() + distance);
    }

    /**
     * @return the block to the east.
     *
     * @since 1.6.5
     */
    public BlockPosHelper east() {
        return new BlockPosHelper(getX() + 1, getY(), getZ());
    }

    /**
     * @param distance the distance to move east
     * @return the n-th block to the east.
     *
     * @since 1.6.5
     */
    public BlockPosHelper east(int distance) {
        return new BlockPosHelper(getX() + distance, getY(), getZ());
    }

    /**
     * @return the block to the west.
     *
     * @since 1.6.5
     */
    public BlockPosHelper west() {
        return new BlockPosHelper(getX() - 1, getY(), getZ());
    }

    /**
     * @param distance the distance to move west
     * @return the n-th block to the west.
     *
     * @since 1.6.5
     */
    public BlockPosHelper west(int distance) {
        return new BlockPosHelper(getX() - distance, getY(), getZ());
    }

    /**
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @return the block offset by the given direction.
     *
     * @since 1.6.5
     */
    public BlockPosHelper offset(String direction) {
        return new BlockPosHelper(base.offset(Direction.byName(direction)));
    }

    /**
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param distance the distance to move in the given direction
     * @return the n-th block offset by the given direction.
     *
     * @since 1.6.5
     */
    public BlockPosHelper offset(String direction, int distance) {
        return new BlockPosHelper(base.offset(Direction.byName(direction)));
    }

    /**
     * @param x the x offset
     * @param y the y offset
     * @param z the y offset
     * @return the block offset by the given values.
     *
     * @since 1.8.4
     */
    public BlockPosHelper offset(int x, int y, int z) {
        return new BlockPosHelper(new BlockPos(getX() + x, getY() + y, getZ() + z));
    }

    /**
     * @return the distance of this position to the given entity.
     *
     * @since 1.8.4
     */
    public double distanceTo(EntityHelper<?> entity) {
        return Math.sqrt(base.getSquaredDistance(entity.getRaw().getPos()));
    }

    /**
     * @return the distance of this position to the given position.
     *
     * @since 1.8.4
     */
    public double distanceTo(BlockPosHelper pos) {
        return Math.sqrt(base.getSquaredDistance(pos.base));
    }

    /**
     * @return the distance of this position to the given position.
     *
     * @since 1.8.4
     */
    public double distanceTo(PositionCommon.Pos3D pos) {
        return Math.sqrt(base.getSquaredDistance(pos.getX(), pos.getY(), pos.getZ()));
    }

    /**
     * @return the distance of this position to the given position.
     *
     * @since 1.8.4
     */
    public double distanceTo(float x, float y, float z) {
        return Math.sqrt(base.getSquaredDistance(x, y, z));
    }

    /**
     * @return the {@link PositionCommon.Pos3D} representation of this position.
     *
     * @since 1.8.4
     */
    public PositionCommon.Pos3D toPos3D() {
        return new PositionCommon.Pos3D(base.getX(), base.getY(), base.getZ());
    }

    @Override
    public String toString() {
        return String.format("BlockPosHelper:{\"x\": %d, \"y\": %d, \"z\": %d}", base.getX(), base.getY(), base.getZ());
    }
}
