package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface IInventory {
    int jsmacros$getX();

    int jsmacros$getY();

    Slot jsmacros_getSlotUnder(double x, double y);

}
