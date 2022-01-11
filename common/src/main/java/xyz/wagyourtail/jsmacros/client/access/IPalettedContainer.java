package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.world.chunk.PalettedContainer;

public interface IPalettedContainer<T> {

    IPalettedContainerData<T> getData();
    PalettedContainer.PaletteProvider getPaletteProvider();

}
