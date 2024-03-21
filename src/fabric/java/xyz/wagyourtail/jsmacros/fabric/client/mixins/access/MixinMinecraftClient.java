package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState catchEmptyShapeException(ClientWorld world, BlockPos blockPos) {
        assert world != null;
        BlockState state = world.getBlockState(blockPos);
        if (state.isAir()) {
            return state;
        }
        if (state.getOutlineShape(world, blockPos).isEmpty()) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }


}
