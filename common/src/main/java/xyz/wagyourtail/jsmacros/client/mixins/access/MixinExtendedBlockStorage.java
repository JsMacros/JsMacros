package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IExtenedBlockStorage;

@Mixin(ChunkSection.class)
public class MixinExtendedBlockStorage implements IExtenedBlockStorage {

    @Shadow
    private int containedBlockCount;
    @Shadow
    private int tickableBlockCount;

    @Override
    public int jsmacros_getNonEmptyBlockCount() {
        return containedBlockCount;
    }

    @Override
    public int jsmacros_getRandomTickableBlockCount() {
        return tickableBlockCount;
    }

}
