package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventAttackBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventAttackEntity;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventInteractBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventInteractEntity;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventInventoryChange;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;

import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private MinecraftClient client;

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

    @Inject(at = @At("RETURN"), method = "interactEntity")
    public void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            new EventInteractEntity(hand != Hand.MAIN_HAND, cir.getReturnValue().name(), entity);
        }
    }

    @Inject(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onClickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, ScreenHandler screenHandler, DefaultedList<Slot> defaultedList, int size, List<ItemStack> list) {
        Int2ObjectMap<ItemStackHelper> oldItems = new Int2ObjectOpenHashMap<>();
        Int2ObjectMap<ItemStackHelper> newItems = new Int2ObjectOpenHashMap<>();

        for (int idx = 0; idx < size; idx++) {
            ItemStack oldStack = list.get(idx);
            ItemStack newStack = defaultedList.get(idx).getStack();
            if (!ItemStack.areEqual(oldStack, newStack)) {
                oldItems.put(idx, new ItemStackHelper(oldStack));
                newItems.put(idx, new ItemStackHelper(newStack));
            }
        }
        new EventInventoryChange(Inventory.create(), oldItems.keySet().toIntArray(), oldItems, newItems);
    }
    
}
