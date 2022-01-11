package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IChunkSection;

@Mixin(ChunkSection.class)
public class MixinChunkSelection implements IChunkSection {

    @Shadow private short nonEmptyBlockCount;

    @Shadow private short randomTickableBlockCount;

    @Shadow private short nonEmptyFluidCount;

    @Override
    public short getNonEmptyBlockCount() {
        return nonEmptyBlockCount;
    }

    @Override
    public short getRandomTickableBlockCount() {
        return randomTickableBlockCount;
    }

    @Override
    public short getNonEmptyFluidCount() {
        return nonEmptyFluidCount;
    }
}
