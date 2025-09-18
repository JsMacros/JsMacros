package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
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
     * @return the {@code x} value of the block.
     * @since 1.2.6
     */
    public int getX() {
        return base.getX();
    }

    /**
     * @return the {@code y} value of the block.
     * @since 1.2.6
     */
    public int getY() {
        return base.getY();
    }

    /**
     * @return the {@code z} value of the block.
     * @since 1.2.6
     */
    public int getZ() {
        return base.getZ();
    }

    /**
     * @return the block above.
     * @since 1.6.5
     */
    public BlockPosHelper up() {
        return new BlockPosHelper(getX(), getY() + 1, getZ());
    }

    /**
     * @param distance the distance to move up
     * @return the block n-th block above.
     * @since 1.6.5
     */
    public BlockPosHelper up(int distance) {
        return new BlockPosHelper(getX(), getY() + distance, getZ());
    }

    /**
     * @return the block below.
     * @since 1.6.5
     */
    public BlockPosHelper down() {
        return new BlockPosHelper(getX(), getY() - 1, getZ());
    }

    /**
     * @param distance the distance to move down
     * @return the block n-th block below.
     * @since 1.6.5
     */
    public BlockPosHelper down(int distance) {
        return new BlockPosHelper(getX(), getY() - distance, getZ());
    }

    /**
     * @return the block to the north.
     * @since 1.6.5
     */
    public BlockPosHelper north() {
        return new BlockPosHelper(getX(), getY(), getZ() - 1);
    }

    /**
     * @param distance the distance to move north
     * @return the n-th block to the north.
     * @since 1.6.5
     */
    public BlockPosHelper north(int distance) {
        return new BlockPosHelper(getX(), getY(), getZ() - distance);
    }

    /**
     * @return the block to the south.
     * @since 1.6.5
     */
    public BlockPosHelper south() {
        return new BlockPosHelper(getX(), getY(), getZ() + 1);
    }

    /**
     * @param distance the distance to move south
     * @return the n-th block to the south.
     * @since 1.6.5
     */
    public BlockPosHelper south(int distance) {
        return new BlockPosHelper(getX(), getY(), getZ() + distance);
    }

    /**
     * @return the block to the east.
     * @since 1.6.5
     */
    public BlockPosHelper east() {
        return new BlockPosHelper(getX() + 1, getY(), getZ());
    }

    /**
     * @param distance the distance to move east
     * @return the n-th block to the east.
     * @since 1.6.5
     */
    public BlockPosHelper east(int distance) {
        return new BlockPosHelper(getX() + distance, getY(), getZ());
    }

    /**
     * @return the block to the west.
     * @since 1.6.5
     */
    public BlockPosHelper west() {
        return new BlockPosHelper(getX() - 1, getY(), getZ());
    }

    /**
     * @param distance the distance to move west
     * @return the n-th block to the west.
     * @since 1.6.5
     */
    public BlockPosHelper west(int distance) {
        return new BlockPosHelper(getX() - distance, getY(), getZ());
    }

    /**
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @return the block offset by the given direction.
     * @since 1.6.5
     */
    public BlockPosHelper offset(String direction) {
        return new BlockPosHelper(base.offset(Direction.byId(direction)));
    }

    /**
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param distance  the distance to move in the given direction
     * @return the n-th block offset by the given direction.
     * @since 1.6.5
     */
    public BlockPosHelper offset(String direction, int distance) {
        return new BlockPosHelper(base.offset(Direction.byId(direction), distance));
    }

    /**
     * @param x the x offset
     * @param y the y offset
     * @param z the y offset
     * @return the block offset by the given values.
     * @since 1.8.4
     */
    public BlockPosHelper offset(int x, int y, int z) {
        return new BlockPosHelper(new BlockPos(getX() + x, getY() + y, getZ() + z));
    }

    /**
     * @return the block position converted to the respective nether coordinates.
     * @since 1.8.4
     */
    public BlockPosHelper toNetherCoords() {
        return new BlockPosHelper(getX() / 8, getY(), getZ() / 8);
    }

    /**
     * @return the block position converted to the respective overworld coordinates.
     * @since 1.8.4
     */
    public BlockPosHelper toOverworldCoords() {
        return new BlockPosHelper(getX() * 8, getY(), getZ() * 8);
    }

    /**
     * @param entity the entity to get the distance to
     * @return the distance of this position to the given entity.
     * @since 1.8.4
     */
    public double distanceTo(EntityHelper<?> entity) {
        return Math.sqrt(base.getSquaredDistance(entity.getRaw().getPos()));
    }

    /**
     * @param pos the position to get the distance to
     * @return the distance of this position to the given position.
     * @since 1.8.4
     */
    public double distanceTo(BlockPosHelper pos) {
        return Math.sqrt(base.getSquaredDistance(pos.base));
    }

    /**
     * @param pos the position to get the distance to
     * @return the distance of this position to the given position.
     * @since 1.8.4
     */
    public double distanceTo(Pos3D pos) {
        return Math.sqrt(base.getSquaredDistance(pos.getX(), pos.getY(), pos.getZ()));
    }

    /**
     * @param x the x coordinate to get the distance to
     * @param y the y coordinate to get the distance to
     * @param z the z coordinate to get the distance to
     * @return the distance of this position to the given position.
     * @since 1.8.4
     */
    public double distanceTo(double x, double y, double z) {
        return Math.sqrt(base.getSquaredDistance(x, y, z));
    }

    /**
     * @return the {@link Pos3D} representation of this position.
     * @since 1.8.4
     */
    public Pos3D toPos3D() {
        return new Pos3D(base.getX(), base.getY(), base.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockPosHelper) {
            BlockPosHelper other = (BlockPosHelper) obj;
            return base.equals(other.base);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    public String toString() {
        return String.format("BlockPosHelper:{\"x\": %d, \"y\": %d, \"z\": %d}", base.getX(), base.getY(), base.getZ());
    }

}
