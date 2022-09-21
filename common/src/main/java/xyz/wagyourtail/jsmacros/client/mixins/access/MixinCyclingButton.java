package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.CyclingButtonWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ICyclingButtonWidget;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CyclingButtonWidget.class)
public abstract class MixinCyclingButton implements ICyclingButtonWidget {

    @Shadow protected abstract void cycle(int amount);

    @Override
    public void jsmacros_cycle(int amount) {
        cycle(amount);
    }
}