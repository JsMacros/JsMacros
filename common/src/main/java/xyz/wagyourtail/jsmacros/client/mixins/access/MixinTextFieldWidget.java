package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.TextFieldWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ITextFieldWidget;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget implements ITextFieldWidget {

    @Shadow private boolean editable;

    @Shadow private int maxLength;

    @Override
    public boolean jsmacros_isEditable() {
        return editable;
    }

    @Override
    public int jsmacros_getMaxLength() {
        return maxLength;
    }
}
