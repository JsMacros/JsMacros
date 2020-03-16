package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.BlockPos;
import xyz.wagyourtail.jsmacros.reflector.BlockDataHelper;
import xyz.wagyourtail.jsmacros.reflector.PlayerHelper;
import xyz.wagyourtail.jsmacros.reflector.PlayerListHelper;

public class worldFunctions {
    public ArrayList<PlayerHelper> getLoadedPlayers() {
        ArrayList<PlayerHelper> players = new ArrayList<>();
        for (AbstractClientPlayerEntity p : MinecraftClient.getInstance().world.getPlayers()) {
            players.add(new PlayerHelper(p));
        }
        return players;
    }
    
    public ArrayList<PlayerListHelper> getPlayers() {
        ArrayList<PlayerListHelper> players = new ArrayList<>();
        for (PlayerListEntry p : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
            players.add(new PlayerListHelper(p));
        }
        return players;
    }
    
    public BlockDataHelper getBlock(int x, int y, int z) {
        BlockState b = MinecraftClient.getInstance().world.getBlockState(new BlockPos(x,y,z));
        BlockEntity t = MinecraftClient.getInstance().world.getBlockEntity(new BlockPos(x,y,z));
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b.getBlock(), t);
        
    }
}
