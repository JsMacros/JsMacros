package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.Palette;

public interface IPalettedContainerData<T> {

    PaletteStorage jsmacros_getStorage();

    Palette<T> jsmacros_getPalette();

}
