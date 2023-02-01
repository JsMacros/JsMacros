package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventInventoryChange;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(Container.class)
public class MixinScreenHandler {

    @Shadow
    @Final
    public DefaultedList<Slot> slots;

    @Inject(method = "updateSlotStacks", at = @At(value = "HEAD"))
    public void onUpdateSlotStacks(List<ItemStack> stacks, CallbackInfo ci) {
        // Only called from onInventory in ClientPlayerNetworkHandler
        int[] slots = JsMacros.range(stacks.size());
        ItemStackHelper[] oldItems = Arrays.stream(slots).mapToObj(slot -> this.slots.get(slot).getStack()).map(ItemStackHelper::new).toArray(ItemStackHelper[]::new);
        ItemStackHelper[] newItems = stacks.stream().map(ItemStackHelper::new).toArray(ItemStackHelper[]::new);
        new EventInventoryChange(Inventory.create(), slots, oldItems, newItems);
    }

    @Inject(method = "setStackInSlot", at = @At(value = "HEAD"))
    public void onSetStackInSlot(int slot, ItemStack stack, CallbackInfo ci) {
        // Only called from onScreenHandlerSlotUpdate in ClientPlayerNetworkHandler
        new EventInventoryChange(Inventory.create(), new int[]{slot}, new ItemStackHelper(this.slots.get(slot).getStack()), new ItemStackHelper(stack));
    }

}