package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventClickSlot;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDropSlot;

@Mixin(ContainerScreen.class)
public class MixinHandledScreen {

    @Inject(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;method_2906(IIILnet/minecraft/container/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void beforeMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        EventClickSlot event = new EventClickSlot((ContainerScreen<?>) (Object) this, actionType.ordinal(), button, slotId);
        if (event.cancel) {
            ci.cancel();
        }
        if (actionType == SlotActionType.THROW || slotId == -999) {
            EventDropSlot eventDrop = new EventDropSlot((ContainerScreen<?>) (Object) this, slotId, button == 1);
            if (eventDrop.cancel) {
                ci.cancel();
            }
        }
    }

}
