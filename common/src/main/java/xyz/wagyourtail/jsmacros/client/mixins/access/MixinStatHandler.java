package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.TupleIntJsonSerializable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StatFileWriter.class)
public interface MixinStatHandler {
    @Accessor
    Map<StatBase, TupleIntJsonSerializable> getField_150875_a();
}
