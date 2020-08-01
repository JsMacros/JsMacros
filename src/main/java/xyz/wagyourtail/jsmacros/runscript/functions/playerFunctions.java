package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import xyz.wagyourtail.jsmacros.compat.interfaces.ISignEditScreen;
import xyz.wagyourtail.jsmacros.reflector.BlockDataHelper;
import xyz.wagyourtail.jsmacros.reflector.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.reflector.EntityHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.Inventory;

public class playerFunctions extends Functions {
    
    public playerFunctions(String libName) {
        super(libName);
    }
    
    public playerFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
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
    
    public boolean writeSign(String l1, String l2, String l3, String l4) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof SignEditScreen) {
            ((ISignEditScreen)mc.currentScreen).setLine(0, l1);
            ((ISignEditScreen)mc.currentScreen).setLine(1, l2);
            ((ISignEditScreen)mc.currentScreen).setLine(2, l3);
            ((ISignEditScreen)mc.currentScreen).setLine(3, l4);
            return true;
        }
        return false;
    }
}
