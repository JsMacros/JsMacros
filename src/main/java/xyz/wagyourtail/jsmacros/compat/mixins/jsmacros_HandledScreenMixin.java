package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import xyz.wagyourtail.jsmacros.compat.interfaces.IInventory;

@Mixin(HandledScreen.class)
class jsmacros_HandledScreenMixin implements IInventory {

    @Shadow
    private Slot getSlotAt(double x, double y) {
        return null;
    }
    
    @Override
    public Slot getSlotUnder(double x, double y) {
        return getSlotAt(x, y);
    }
    
}
