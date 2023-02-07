package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface IInventory {
    @Accessor
    int getX();

    @Accessor
    int getY();

    Slot jsmacros_getSlotUnder(double x, double y);
}
