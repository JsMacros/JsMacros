package xyz.wagyourtail.jsmacros.forge.client.mixins.access;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow @Nullable public ClientWorld world;

    @SuppressWarnings("ALL")
    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;isAir(Lnet/minecraft/util/math/BlockPos;)Z"))
    @Group(name = "catchEmptyShapeException", min = 1, max = 1)
    private boolean catchEmptyShapeExceptionNeoForge(ClientWorld instance, BlockPos blockPos) {
        if (instance.isAir(blockPos)) return true;
        assert world != null;
        return world.getBlockState(blockPos).getOutlineShape(world, blockPos).isEmpty();
    }

    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    @Group(name = "catchEmptyShapeException", min = 1, max = 1)
    private BlockState catchEmptyShapeExceptionLexForge(ClientWorld world, BlockPos blockPos) {
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
