package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventAttackBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventAttackEntity;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventInteractBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventInteractEntity;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "method_13842")
    public void onInteractBlock(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, BlockPos blockPos, Direction direction, Vec3d vec3d, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            new EventInteractBlock(
                hand != Hand.MAIN_HAND,
                cir.getReturnValue().name(),
                new BlockDataHelper(clientWorld.getBlockState(blockPos), clientWorld.getBlockEntity(blockPos), blockPos),
                direction.getId()
            );
        }
    }

    @Inject(at = @At("RETURN"), method = "attackBlock")
    public void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            assert client.world != null;
            new EventAttackBlock(
                new BlockDataHelper(client.world.getBlockState(pos), client.world.getBlockEntity(pos), pos),
                direction.getId()
            );
        }
    }

    @Inject(at = @At("RETURN"), method = "attackEntity")
    public void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        new EventAttackEntity(target);
    }

    @Inject(at = @At("RETURN"), method = "method_12235")
    public void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            new EventInteractEntity(hand != Hand.MAIN_HAND, cir.getReturnValue().name(), entity);
        }
    }


    @Inject(at = @At("RETURN"), method = "method_12236")
    public void onInteractEntity(PlayerEntity playerEntity, Entity entity, HitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            new EventInteractEntity(hand != Hand.MAIN_HAND, cir.getReturnValue().name(), entity);
        }
    }
}
