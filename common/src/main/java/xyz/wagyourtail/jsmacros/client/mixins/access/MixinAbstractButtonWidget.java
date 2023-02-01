package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractButtonWidget.class)
public interface MixinAbstractButtonWidget {

    @Accessor
    int getHeight();

}
