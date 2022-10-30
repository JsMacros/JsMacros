package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.class_2743;
import net.minecraft.class_2748;
import net.minecraft.class_2928;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;

@Mixin(class_2743.class)
public class MixinPalettedContainer implements IPalettedContainer {

    @Shadow protected class_2928 field_12904;

    @Shadow protected class_2748 field_12905;

    @Override
    public class_2928 jsmacros_getData() {
        return field_12904;
    }

    @Override
    public class_2748 jsmacros_getPaletteProvider() {
        return field_12905;
    }

}
