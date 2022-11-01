package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.util.ItemAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventClickSlot;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventDropSlot;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

    @Inject(method = "method_1131", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;method_1224(IIILnet/minecraft/util/ItemAction;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void beforeMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        EventClickSlot event = new EventClickSlot((ContainerScreen<?>) (Object) this, actionType.ordinal(), button, slotId);
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        if (actionType == SlotActionType.THROW || slotId == -999) {
            EventDropSlot eventDrop = new EventDropSlot((ContainerScreen<?>) (Object) this, slotId, button == 1);
            if (eventDrop.isCanceled()) {
                ci.cancel();
            }
        }
    }

}
