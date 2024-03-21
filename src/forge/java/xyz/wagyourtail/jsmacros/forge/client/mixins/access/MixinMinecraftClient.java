package xyz.wagyourtail.jsmacros.forge.client.mixins.access;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow @Nullable public ClientWorld world;

    @ModifyExpressionValue(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;isAir(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean catchEmptyShapeExceptionForge(boolean value, @Local BlockPos blockPos) {
        if (value) return true;
        assert world != null;
        return world.getBlockState(blockPos).getOutlineShape(world, blockPos).isEmpty();
    }

}
