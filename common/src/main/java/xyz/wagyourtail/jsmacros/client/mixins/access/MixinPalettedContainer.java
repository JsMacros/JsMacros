package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;

@Mixin(PalettedContainer.class)
public class MixinPalettedContainer<T> implements IPalettedContainer<T> {

    @Shadow private Palette<T> palette;

    @Shadow protected PackedIntegerArray data;

    @Override
    public PackedIntegerArray jsmacros_getData() {
        return data;
    }

    @Override
    public Palette<?> jsmacros_getPaletteProvider() {
        return palette;
    }

}
