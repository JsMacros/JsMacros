package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.collection.PackedIntegerArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;

@Mixin(PackedIntegerArray.class)
public class MixinPackedIntegerArray implements IPackedIntegerArray {
    @Shadow @Final private long maxValue;

    @Shadow @Final private int elementsPerLong;

    @Shadow @Final private int indexScale;

    @Shadow @Final private int indexOffset;

    @Shadow @Final private int indexShift;

    @Override
    public long getMaxValue() {
        return maxValue;
    }

    @Override
    public int getElementsPerLong() {
        return elementsPerLong;
    }

    @Override
    public int getIndexScale() {
        return indexScale;
    }

    @Override
    public int getIndexOffset() {
        return indexOffset;
    }

    @Override
    public int getIndexShift() {
        return indexShift;
    }

}
