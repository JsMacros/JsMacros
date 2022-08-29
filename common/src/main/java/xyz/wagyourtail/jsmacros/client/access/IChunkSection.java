package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.PalettedContainer;

public interface IChunkSection {

    short jsmacros_getNonEmptyBlockCount();
    short jsmacros_getRandomTickableBlockCount();
    short jsmacros_getNonEmptyFluidCount();
    PalettedContainer<BlockState> jsmacros_getContainer();
}
