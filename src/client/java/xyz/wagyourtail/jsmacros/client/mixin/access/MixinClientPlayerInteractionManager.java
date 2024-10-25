package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IClientPlayerInteractionManager;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;

@Mixin(ClientPlayerInteractionManager.class)
class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int blockBreakingCooldown;

    @Inject(at = @At("RETURN"), method = "breakBlock")
    public void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        InteractionProxy.Break.onBreakBlock(pos, cir.getReturnValueZ());
    }

    @Inject(at = @At("RETURN"), method = "updateBlockBreakingProgress")
    public void breakingBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()
                && InteractionProxy.Break.isBreaking()
                && client.crosshairTarget != null
                && client.crosshairTarget.getType() == HitResult.Type.BLOCK
                && ((BlockHitResult) client.crosshairTarget).getBlockPos().equals(pos)
        ) InteractionProxy.Break.setOverride(false, "NOT_BREAKING");
    }

    @Override
    public int jsmacros_getBlockBreakingCooldown() {
        return blockBreakingCooldown;
    }

}
