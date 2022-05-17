package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface IClientPlayerInteractionManager {

    boolean jsmacros_isBreakingBlock();
    BlockPos jsmacros_getCurrentBreakingPos();

    void jsmacros_setCurrentBreakingPos(BlockPos currentBreakingPos);
    void jsmacros_setBreakingBlock(boolean breakingBlock);

    void jsmacros_sendPlayerAction(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction);

}
