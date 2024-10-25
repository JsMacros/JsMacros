package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(TextFieldWidget.class)
public interface MixinTextFieldWidget {

    @Accessor
    boolean getEditable();

    @Accessor
    int getMaxLength();

}
