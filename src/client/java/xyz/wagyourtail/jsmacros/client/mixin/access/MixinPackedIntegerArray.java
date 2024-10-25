package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.util.collection.PackedIntegerArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;

@Mixin(PackedIntegerArray.class)
public class MixinPackedIntegerArray implements IPackedIntegerArray {
    @Shadow
    @Final
    private long maxValue;

    @Shadow
    @Final
    private int elementsPerLong;

    @Shadow
    @Final
    private int indexScale;

    @Shadow
    @Final
    private int indexOffset;

    @Shadow
    @Final
    private int indexShift;

    @Override
    public long jsmacros_getMaxValue() {
        return maxValue;
    }

    @Override
    public int jsmacros_getElementsPerLong() {
        return elementsPerLong;
    }

    @Override
    public int jsmacros_getIndexScale() {
        return indexScale;
    }

    @Override
    public int jsmacros_getIndexOffset() {
        return indexOffset;
    }

    @Override
    public int jsmacros_getIndexShift() {
        return indexShift;
    }

}
