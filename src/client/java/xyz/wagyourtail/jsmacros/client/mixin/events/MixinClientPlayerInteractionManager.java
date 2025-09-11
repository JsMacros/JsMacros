package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventAttackBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventAttackEntity;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventInteractBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventInteractEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockDataHelper;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "interactBlock")
    public void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            BlockPos pos = hitResult.getBlockPos();
            new EventInteractBlock(
                    hand != Hand.MAIN_HAND,
                    cir.getReturnValue().isAccepted(),
                    new BlockDataHelper(player.getWorld().getBlockState(pos), player.getWorld().getBlockEntity(pos), pos),
                    hitResult.getSide().getIndex()
            ).trigger();
        }
    }

    @Inject(at = @At("RETURN"), method = "attackBlock")
    public void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            assert client.world != null;
            new EventAttackBlock(
                    new BlockDataHelper(client.world.getBlockState(pos), client.world.getBlockEntity(pos), pos),
                    direction.getIndex()
            ).trigger();
        }
    }

    @Inject(at = @At("RETURN"), method = "attackEntity")
    public void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        new EventAttackEntity(target).trigger();
    }

    @Inject(at = @At("RETURN"), method = "interactEntity")
    public void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            new EventInteractEntity(hand != Hand.MAIN_HAND, cir.getReturnValue().isAccepted(), entity).trigger();
        }
    }

}
