package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventInteractBlock;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(at = @At("RETURN"), method = "interactBlock")
    public void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            BlockPos pos = hitResult.getBlockPos();
            new EventInteractBlock(
                hand != Hand.MAIN_HAND,
                cir.getReturnValue().name(),
                new BlockDataHelper(player.world.getBlockState(pos), player.world.getBlockEntity(pos), pos),
                hitResult.getSide().getId()
            );
        }
    }
}
