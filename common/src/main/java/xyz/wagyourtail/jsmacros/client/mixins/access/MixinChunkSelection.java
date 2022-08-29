package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IChunkSection;

@Mixin(ChunkSection.class)
public class MixinChunkSelection implements IChunkSection {

    @Shadow private short nonEmptyBlockCount;

    @Shadow private short randomTickableBlockCount;

    @Shadow private short nonEmptyFluidCount;

    @Shadow @Final private PalettedContainer<BlockState> container;

    @Override
    public short jsmacros_getNonEmptyBlockCount() {
        return nonEmptyBlockCount;
    }

    @Override
    public short jsmacros_getRandomTickableBlockCount() {
        return randomTickableBlockCount;
    }

    @Override
    public short jsmacros_getNonEmptyFluidCount() {
        return nonEmptyFluidCount;
    }

    @Override
    public PalettedContainer<BlockState> jsmacros_getContainer() {
        return container;
    }
}
