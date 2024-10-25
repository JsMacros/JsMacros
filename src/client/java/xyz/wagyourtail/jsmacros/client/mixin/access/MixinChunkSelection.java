package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IChunkSection;

@Mixin(ChunkSection.class)
public class MixinChunkSelection implements IChunkSection {

    @Shadow
    private short nonEmptyBlockCount;

    @Shadow
    private short randomTickableBlockCount;

    @Shadow
    private short nonEmptyFluidCount;

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

}
