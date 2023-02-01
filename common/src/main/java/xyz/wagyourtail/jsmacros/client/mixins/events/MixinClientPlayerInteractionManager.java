package xyz.wagyourtail.jsmacros.client.mixins.events;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
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
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventAttackBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventAttackEntity;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventInteractBlock;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventInteractEntity;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventInventoryChange;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockDataHelper;

import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "interactBlock")
    public void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.FAIL) {
            BlockPos pos = hitResult.getBlockPos();
            new EventInteractBlock(
                hand != Hand.MAIN_HAND,
                cir.getReturnValue().name(),
                new BlockDataHelper(world.getBlockState(pos), world.getBlockEntity(pos), pos),
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

    private static boolean areEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        } else {
            return !left.isEmpty() && !right.isEmpty() ? isEqual(left, right) : false;
        }
    }
    private static boolean isEqual(ItemStack left, ItemStack right) {
        if (left.getCount() != right.getCount()) {
            return false;
        } else if (!left.getItem().equals(right.getItem())) {
            return false;
        } else if (left.getTag() == null && right.getTag() != null) {
            return false;
        } else {
            return left.getTag() == null || left.getTag().equals(right.getTag());
        }
    }

    @Inject(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/container/Container;onSlotClick(IILnet/minecraft/container/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onClickSlot(int syncId, int slotId, int mouseButton, SlotActionType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir, short s) {
        Int2ObjectMap<ItemStackHelper> oldItems = new Int2ObjectOpenHashMap<>();
        Int2ObjectMap<ItemStackHelper> newItems = new Int2ObjectOpenHashMap<>();

        Container screenHandler = player.container;
        List<Slot> defaultedList = screenHandler.slots;
        int size = defaultedList.size();
        List<ItemStack> list = Lists.newArrayListWithCapacity(size);
        for (Slot slot : defaultedList) {
            list.add(slot.getStack().copy());
        }

        for (int idx = 0; idx < size; idx++) {
            ItemStack oldStack = list.get(idx);
            ItemStack newStack = defaultedList.get(idx).getStack();
            if (!areEqual(oldStack, newStack)) {
                oldItems.put(idx, new ItemStackHelper(oldStack));
                newItems.put(idx, new ItemStackHelper(newStack));
            }
        }
        new EventInventoryChange(Inventory.create(), oldItems.keySet().toIntArray(), oldItems, newItems);
    }
    
}
