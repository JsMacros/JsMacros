package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    @Shadow private BlockPos currentBreakingPos;
    @Shadow private boolean breakingBlock;


    @Shadow protected abstract void sendPlayerAction(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction);

    @Override
    public BlockPos jsmacros_getCurrentBreakingPos() {
        return currentBreakingPos;
    }

    @Override
    public void jsmacros_setCurrentBreakingPos(BlockPos currentBreakingPos) {
        this.currentBreakingPos = currentBreakingPos;
    }

    @Override
    public boolean jsmacros_isBreakingBlock() {
        return breakingBlock;
    }

    @Override
    public void jsmacros_setBreakingBlock(boolean breakingBlock) {
        this.breakingBlock = breakingBlock;
    }

    @Override
    public void jsmacros_sendPlayerAction(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction) {
        this.sendPlayerAction(action, pos, direction);
    }
}
