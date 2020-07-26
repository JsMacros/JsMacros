package xyz.wagyourtail.jsmacros.reflector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockDataHelper {
    private Block b;
    private BlockState bs;
    private BlockPos bp;
    private BlockEntity e;
    
    public BlockDataHelper(BlockState b, BlockEntity e, BlockPos bp) {
        this.b = b.getBlock();
        this.bp = bp;
        this.bs = b;
        this.e = e;
    }
    
    public int getX() {
        return bp.getX();
    }
    
    public int getY() {
        return bp.getY();
    }
    
    public int getZ() {
        return bp.getZ();
    }
    
    public String getId() {
        return Registry.BLOCK.getId(b).toString();
    }
    
    public String getName() {
        return b.getName().toString();
    }
    
    public Map<String, String> getNBT() {
        if (e == null) return null;
        HashMap<String, String> m = new HashMap<>();
        CompoundTag t = e.toInitialChunkDataTag();
        for (String s : t.getKeys()) {
            m.put(s, t.get(s).asString());
        }
        return m;
    }
    
    public Map<String, String> getBlockState() {
        HashMap<String, String> map = new HashMap<>();
        for (Entry<Property<?>, Comparable<?>> e : bs.getEntries().entrySet()) {
            map.put(e.getKey().getName(), Util.getValueAsString(e.getKey(), e.getValue()));
        }
        return map;
    }
    
    public Block getRawBlock() {
        return b;
    }
    
    public BlockState getRawBlockState() {
        return bs;
    }
    
    public BlockEntity getRawBlockEntity() {
        return e;
    }
    
    public String toString() {
        return String.format("BlockDataHelper:{\"x\":%d, \"y\":%d, \"z\":%d, \"id\":\"%s\"}", bp.getX(), bp.getY(), bp.getZ(), this.getId());
    }
}
