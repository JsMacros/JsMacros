package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.util.ItemAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventClickSlot;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDropSlot;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

    @Inject(method = "method_1131", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;method_1224(IIILnet/minecraft/util/ItemAction;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void beforeMouseClick(Slot slot, int slotId, int button, ItemAction actionType, CallbackInfo ci) {
        EventClickSlot event = new EventClickSlot((HandledScreen) (Object) this, actionType.ordinal(), button, slotId);
        if (event.cancel) {
            ci.cancel();
        }
        if (actionType == ItemAction.THROW || slotId == -999) {
            EventDropSlot eventDrop = new EventDropSlot((HandledScreen) (Object) this, slotId, button == 1);
            if (eventDrop.cancel) {
                ci.cancel();
            }
        }
    }

}
