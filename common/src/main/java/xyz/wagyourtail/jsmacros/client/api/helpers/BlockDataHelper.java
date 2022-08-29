package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class BlockDataHelper extends BaseHelper<BlockState> {
    private final Block b;
    private final BlockPos bp;
    private final BlockEntity e;
    
    public BlockDataHelper(BlockState b, BlockEntity e, BlockPos bp) {
        super(b);
        this.b = b.getBlock();
        this.bp = bp;
        this.e = e;
    }
    
    /**
     * @since 1.1.7
     * 
     * @return the {@code x} value of the block.
     */
    public int getX() {
        return bp.getX();
    }
    
    /**
     * @since 1.1.7
     * 
     * @return the {@code y} value of the block.
     */
    public int getY() {
        return bp.getY();
    }
    
    /**
     * @since 1.1.7
     * 
     * @return the {@code z} value of the block.
     */
    public int getZ() {
        return bp.getZ();
    }
    
    /**
     * @return the item ID of the block.
     */
    public String getId() {
        return Registry.BLOCK.getId(b).toString();
    }
    
    /**
     * @return the translated name of the block. (was string before 1.6.5)
     */
    public TextHelper getName() {
        return new TextHelper(b.getName());
    }

    /**
     * @return
     * @since 1.5.1, used to be a {@link Map}&lt;{@link String}, {@link String}&gt;
     */
    public NBTElementHelper<?> getNBT() {
        if (e == null) return null;
        NbtCompound nbt = new NbtCompound();
        e.writeNbt(nbt);
        return NBTElementHelper.resolve(nbt);
    }

    /**
     * @return
     *
     * @since 1.6.5
     */
    public BlockStateHelper getBlockStateHelper() {
        return new BlockStateHelper(base);
    }

    /**
     * @return
     *
     * @since 1.6.5
     */
    public BlockHelper getBlockHelper() {
        return new BlockHelper(base.getBlock());
    }
    
    /**
     * @since 1.1.7
     * 
     * @return block state data as a {@link Map}.
     */
    public Map<String, String> getBlockState() {
        Map<String, String> map = new HashMap<>();
        for (Entry<Property<?>, Comparable<?>> e : base.getEntries().entrySet()) {
            map.put(e.getKey().getName(), Util.getValueAsString(e.getKey(), e.getValue()));
        }
        return map;
    }
    
    /**
     * @since 1.2.7
     * 
     * @return the block pos.
     */
    public BlockPosHelper getBlockPos() {
        return new BlockPosHelper(bp);
    }
    
    public Block getRawBlock() {
        return b;
    }
    
    public BlockState getRawBlockState() {
        return base;
    }
    
    public BlockEntity getRawBlockEntity() {
        return e;
    }
    
    @Override
    public String toString() {
        return String.format("BlockDataHelper:{\"x\":%d, \"y\":%d, \"z\":%d, \"id\":\"%s\"}", bp.getX(), bp.getY(), bp.getZ(), this.getId());
    }
}
