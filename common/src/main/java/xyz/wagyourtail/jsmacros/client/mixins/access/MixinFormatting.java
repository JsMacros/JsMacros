package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Formatting.class)
public interface MixinFormatting {

    @Accessor
    char getCode();
}
