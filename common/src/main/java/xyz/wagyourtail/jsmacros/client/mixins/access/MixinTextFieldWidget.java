package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ITextFieldWidget;

@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget implements ITextFieldWidget {
    @Shadow @Final @Mutable
    private int width;

    @Override
    public void jsmacros_setWidth(int width) {
        this.width = width;
    }
}
