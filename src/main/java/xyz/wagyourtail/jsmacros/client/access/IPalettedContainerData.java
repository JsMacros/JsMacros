package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.world.chunk.Palette;

public interface IPalettedContainerData<T> {

    PackedIntegerArray jsmacros_getStorage();

    Palette<T> jsmacros_getPalette();

}
