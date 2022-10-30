package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IGuiTextField;

import java.util.function.Consumer;

@Mixin(TextFieldWidget.class)
public abstract class MixinGuiTextField implements IGuiTextField {
    @Shadow
    private String text;
    @Unique
    Consumer<String> onChange;

    @Override
    @Accessor("editable")
    public abstract boolean isEnabled();

    @Override
    public void setOnChange(Consumer<String> onChange) {
        this.onChange = onChange;
    }

    @Inject(at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;moveCursor(I)V",
        shift = At.Shift.AFTER), method = "write")
    public void onWriteText(String p_146191_1_, CallbackInfo ci) {
        if (onChange != null)
            onChange.accept(this.text);
    }
}
