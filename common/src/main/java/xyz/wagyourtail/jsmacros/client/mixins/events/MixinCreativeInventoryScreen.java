package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventClickSlot;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDropSlot;

import java.lang.reflect.Field;
import java.util.Arrays;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen {

    @Shadow protected abstract boolean isCreativeInventorySlot(@Nullable Slot slot);

    @Unique
    private static Class<? extends Slot> lockableSlot;

    @Unique
    private static Class<? extends Slot> creativeSlot;

    @Unique
    private static Field slotInCreativeSlot;

    @Unique
    private synchronized Slot getSlotFromCreativeSlot(Slot in) {
        if (in.getClass().equals(Slot.class)) return in;
        boolean lockable = in.getClass().equals(lockableSlot);
        boolean creative = in.getClass().equals(creativeSlot);
        if (lockable) {
            return in;
        }
        if (creative) {
            try {
                return (Slot) slotInCreativeSlot.get(in);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (lockableSlot == null || creativeSlot == null) {
            // define creative/lockable slot classes
            try {
                Class<? extends Slot> unknown = in.getClass();
                Field slotField = Arrays.stream(unknown.getDeclaredFields())
                    .filter(e -> e.getType().equals(Slot.class))
                    .findFirst()
                    .orElse(null);
                if (slotField == null)
                    lockableSlot = unknown;
                else {
                    slotInCreativeSlot = slotField;
                    slotInCreativeSlot.setAccessible(true);
                    creativeSlot = unknown;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return getSlotFromCreativeSlot(in);
        }
        throw new NullPointerException("Unknown slot class");
    }

    @Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
    public void beforeMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot != null) slotId = getSlotFromCreativeSlot(slot).id;
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
