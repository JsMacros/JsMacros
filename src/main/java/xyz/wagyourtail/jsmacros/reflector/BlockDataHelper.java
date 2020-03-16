package xyz.wagyourtail.jsmacros.reflector;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public class BlockDataHelper {
    private Block b;
    private BlockEntity e;
    
    public BlockDataHelper(Block b, BlockEntity e) {
        this.b = b;
        this.e = e;
    }
    
    public String getId() {
        return Registry.BLOCK.getId(b).toString();
    }
    
    public String getName() {
        return b.getName().asFormattedString();
    }
    
    public HashMap<String, String> getNBT() {
        if (e == null) return null;
        HashMap<String, String> m = new HashMap<>();
        CompoundTag t = e.toInitialChunkDataTag();
        for (String s : t.getKeys()) {
            m.put(s, t.get(s).asString());
        }
        return m;
    }
    
    public Block getRawBlock() {
        return b;
    }
    
    public BlockEntity getRawBlockEntity() {
        return e;
    }
}
