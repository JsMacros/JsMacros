package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IInventory;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen implements IInventory {

    @Shadow protected abstract Slot getSlotAt(int x, int y);

    @Override
    public Slot jsmacros_getSlotUnder(int x, int y) {
        return getSlotAt(x, y);
    }

}
