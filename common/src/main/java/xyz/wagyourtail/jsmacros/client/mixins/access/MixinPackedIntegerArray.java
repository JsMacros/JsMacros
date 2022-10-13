package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.collection.PackedIntegerArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;

@Mixin(PackedIntegerArray.class)
public class MixinPackedIntegerArray implements IPackedIntegerArray {
    @Shadow @Final private long maxValue;

    @Shadow @Final private int field_24079;

    @Shadow @Final private int field_24080;

    @Shadow @Final private int field_24081;

    @Shadow @Final private int field_24082;

    @Shadow @Final private int elementBits;

    @Override
    public long jsmacros_getMaxValue() {
        return maxValue;
    }

    @Override
    public int jsmacros_getElementsPerLong() {
        return field_24079;
    }

    @Override
    public int jsmacros_getIndexScale() {
        return field_24080;
    }

    @Override
    public int jsmacros_getIndexOffset() {
        return field_24081;
    }

    @Override
    public int jsmacros_getIndexShift() {
        return field_24082;
    }

    @Override
    public int jsmacros_getElementBits() {
        return elementBits;
    }

}
