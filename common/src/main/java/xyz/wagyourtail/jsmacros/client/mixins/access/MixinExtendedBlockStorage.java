package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IExtenedBlockStorage;

@Mixin(ExtendedBlockStorage.class)
public class MixinExtendedBlockStorage implements IExtenedBlockStorage {

    @Shadow
    private int field_76682_b;
    @Shadow
    private int field_76683_c;

    @Override
    public int jsmacros_getNonEmptyBlockCount() {
        return field_76682_b;
    }

    @Override
    public int jsmacros_getRandomTickableBlockCount() {
        return field_76683_c;
    }

}
