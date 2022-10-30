package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.class_2928;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_2928.class)
public class MixinPackedIntegerArray {
    @Shadow @Final private int field_14369;

    @Shadow @Final private long field_14370;

    public long jsmacros_getMaxValue() {
        return field_14370;
    }

    public int jsmacros_getElementBits() {
        return field_14369;
    }
}
