package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface IInventory {
    @Accessor
    int jsmacros$getX();

    @Accessor
    int jsmacros$getY();

    Slot jsmacros_getSlotUnder(double x, double y);

}
