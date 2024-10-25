package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleOption.class)
public interface MixinSimpleOption {
    @Accessor("value")
    <T> void forceSetValue(T value);

}
