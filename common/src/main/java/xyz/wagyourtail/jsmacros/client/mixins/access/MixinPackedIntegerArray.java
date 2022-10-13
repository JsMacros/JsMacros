package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.PackedIntegerArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;

@Mixin(PackedIntegerArray.class)
public class MixinPackedIntegerArray implements IPackedIntegerArray {
    @Shadow @Final private long maxValue;

    @Override
    public long jsmacros_getMaxValue() {
        return maxValue;
    }
}
