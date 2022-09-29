package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.hit.BlockHitResult;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBlockHitResult;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(BlockHitResult.class)
public class MixinBlockHitResult implements IBlockHitResult {

    @Shadow
    @Final
    private boolean missed;

    @Override
    public boolean jsmacros_missed() {
        return missed;
    }

}
