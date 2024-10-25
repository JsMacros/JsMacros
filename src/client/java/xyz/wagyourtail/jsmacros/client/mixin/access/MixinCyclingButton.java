package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CyclingButtonWidget.class)
public interface MixinCyclingButton<T> {

    @Invoker
    void invokeCycle(int amount);

    @Invoker
    Text invokeComposeText(T value);

    @Accessor
    Function<T, Text> getValueToText();

}
