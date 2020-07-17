package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import xyz.wagyourtail.jsmacros.reflector.BlockDataHelper;
import xyz.wagyourtail.jsmacros.reflector.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.reflector.EntityHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.Inventory;

public class playerFunctions {
    public Inventory openInventory() {
        return new Inventory();
    }
    
    public ClientPlayerEntityHelper getPlayer() {
    	MinecraftClient mc = MinecraftClient.getInstance();
    	return new ClientPlayerEntityHelper(mc.player);
    }
    
    public String getGameMode() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.interactionManager.getCurrentGameMode().toString();
    }
    
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
    	MinecraftClient mc = MinecraftClient.getInstance();
    	BlockHitResult h = (BlockHitResult) mc.player.rayTrace(distance, 0, fluid);
    	if (h.getType() == HitResult.Type.MISS) return null;
		BlockState b = mc.world.getBlockState(h.getBlockPos());
        BlockEntity t = mc.world.getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }
    
    public EntityHelper rayTraceEntity() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.targetedEntity != null) return new EntityHelper(mc.targetedEntity);
        else return null;
    }
}
