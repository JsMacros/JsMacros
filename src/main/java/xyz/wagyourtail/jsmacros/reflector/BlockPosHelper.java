package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.util.math.BlockPos;

public class BlockPosHelper {
    private BlockPos b;
    
    public BlockPosHelper(BlockPos b) {
        this.b = b;
    }
    
    public int getX() {
        return b.getX();
    }
    
    public int getY() {
        return b.getY();
    }
    
    public int getZ() {
        return b.getZ();
    }
}
