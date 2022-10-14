package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventClickSlot;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDropSlot;

@Mixin(GuiContainer.class)
public class MixinHandledScreen {

    @Inject(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;clickSlot(IIIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void beforeMouseClick(Slot slot, int slotId, int button, int actionType, CallbackInfo ci) {
        EventClickSlot event = new EventClickSlot((GuiContainer) (Object) this, actionType, button, slotId);
        if (event.cancel) {
            ci.cancel();
        }
        if (actionType == 4 || slotId == -999) {
            EventDropSlot eventDrop = new EventDropSlot((GuiContainer) (Object) this, slotId, button == 1);
            if (eventDrop.cancel) {
                ci.cancel();
            }
        }
    }

}
