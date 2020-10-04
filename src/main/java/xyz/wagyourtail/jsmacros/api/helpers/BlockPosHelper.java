package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.util.math.BlockPos;

/**
 * @author Wagyourtail
 * @since 1.2.6
 */
public class BlockPosHelper {
    private BlockPos b;
    
    public BlockPosHelper(BlockPos b) {
        this.b = b;
    }
    
    /**
     * @since 1.2.6
     * @return the {@code x} value of the block.
     */
    public int getX() {
        return b.getX();
    }
    
    /**
     * @since 1.2.6
     * @return the {@code y} value of the block.
     */
    public int getY() {
        return b.getY();
    }
    
    /**
     * @since 1.2.6
     * @return the {@code z} value of the block.
     */
    public int getZ() {
        return b.getZ();
    }
    
    public String toString() {
        return String.format("BlockPosHelper:{\"x\": %d, \"y\": %d, \"z\": %d}", b.getX(), b.getY(), b.getZ());
    }
}
