package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainerData;

@Mixin(targets = "net.minecraft.world.chunk.PalettedContainer$Data")
public class MixinPalettedContainerData<T> implements IPalettedContainerData<T> {

    @Shadow
    @Final
    private PaletteStorage storage;

    @Shadow
    @Final
    private Palette<T> palette;

    @Override
    public PaletteStorage jsmacros_getStorage() {
        return storage;
    }

    @Override
    public Palette<T> jsmacros_getPalette() {
        return palette;
    }

}
