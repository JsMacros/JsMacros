package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
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

@Mixin(PlayerControllerMP.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private Minecraft client;

    @Inject(at = @At("RETURN"), method = "onRightClick")
    public void onInteractBlock(EntityPlayerSP p_onRightClick_1_, WorldClient p_onRightClick_2_, ItemStack p_onRightClick_3_, BlockPos p_onRightClick_4_, EnumFacing p_onRightClick_5_, Vec3 p_onRightClick_6_, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            new EventInteractBlock(
                false,
                cir.getReturnValue().toString(),
                new BlockDataHelper(p_onRightClick_2_.getBlockState(p_onRightClick_4_), p_onRightClick_2_.getBlockEntity(p_onRightClick_4_), p_onRightClick_4_),
                p_onRightClick_5_.getId()
            );
        }
    }

    @Inject(at = @At("RETURN"), method = "attackBlock")
    public void onAttackBlock(BlockPos p_attackBlock_1_, EnumFacing p_attackBlock_2_, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            assert client.world != null;
            new EventAttackBlock(
                new BlockDataHelper(client.world.getBlockState(p_attackBlock_1_), client.world.getBlockEntity(p_attackBlock_1_), p_attackBlock_1_),
                p_attackBlock_2_.getId()
            );
        }
    }

    @Inject(at = @At("RETURN"), method = "attackEntity")
    public void onAttackEntity(EntityPlayer p_attackEntity_1_, Entity p_attackEntity_2_, CallbackInfo ci) {
        new EventAttackEntity(p_attackEntity_2_);
    }

    @Inject(at = @At("RETURN"), method = "interactEntity")
    public void onInteractEntity(EntityPlayer p_interactEntity_1_, Entity p_interactEntity_2_, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            new EventInteractEntity(false, cir.getReturnValue().toString(), p_interactEntity_2_);
        }
    }


    @Inject(at = @At("RETURN"), method = "interactEntityAtLocation")
    public void onInteractEntity(EntityPlayer p_interactEntityAtLocation_1_, Entity p_interactEntityAtLocation_2_, MovingObjectPosition p_interactEntityAtLocation_3_, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            new EventInteractEntity(false, cir.getReturnValue().toString(), p_interactEntityAtLocation_2_);
        }
    }
}
