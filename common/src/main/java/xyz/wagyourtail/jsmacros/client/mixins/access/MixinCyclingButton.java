package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ICyclingButtonWidget;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CyclingButtonWidget.class)
public abstract class MixinCyclingButton<T> implements ICyclingButtonWidget {

    @Shadow protected abstract void cycle(int amount);

    @Shadow protected abstract Text composeText(T value);

    @Shadow public abstract T getValue();

    @Override
    public void jsmacros_cycle(int amount) {
        cycle(amount);
    }

    @Override
    public Text jsmacros_getTextValue() {
        return composeText(getValue());
    }
}
