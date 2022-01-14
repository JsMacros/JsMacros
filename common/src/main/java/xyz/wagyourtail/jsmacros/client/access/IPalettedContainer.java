package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.world.chunk.PalettedContainer;

public interface IPalettedContainer<T> {

    IPalettedContainerData<T> jsmacros_getData();
    PalettedContainer.PaletteProvider jsmacros_getPaletteProvider();

}
