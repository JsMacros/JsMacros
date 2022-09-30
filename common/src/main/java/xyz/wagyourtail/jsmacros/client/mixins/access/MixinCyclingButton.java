package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ICyclingButtonWidget;

import java.util.function.Function;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CyclingButtonWidget.class)
public abstract class MixinCyclingButton<T> implements ICyclingButtonWidget<T> {

    @Shadow
    protected abstract void cycle(int amount);

    @Shadow
    protected abstract Text composeText(T value);

    @Shadow
    public abstract T getValue();

    @Shadow
    @Final
    private Function<T, Text> valueToText;

    @Override
    public void jsmacros_cycle(int amount) {
        cycle(amount);
    }

    @Override
    public Text jsmacros_getTextValue() {
        return composeText(getValue());
    }

    @Override
    public String jsmacros_toString(T val) {
        return valueToText.apply(val).getString();
    }
}
