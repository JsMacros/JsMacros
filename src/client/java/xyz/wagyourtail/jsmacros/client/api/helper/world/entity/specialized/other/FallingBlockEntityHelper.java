package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.other;

import net.minecraft.entity.FallingBlockEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FallingBlockEntityHelper extends EntityHelper<FallingBlockEntity> {

    public FallingBlockEntityHelper(FallingBlockEntity base) {
        super(base);
    }

    /**
     * @return the block position this block is falling from.
     * @since 1.8.4
     */
    public BlockPosHelper getOriginBlockPos() {
        return new BlockPosHelper(base.getFallingBlockPos());
    }

    /**
     * @return the block state of this falling block.
     * @since 1.8.4
     */
    public BlockStateHelper getBlockState() {
        return new BlockStateHelper(base.getBlockState());
    }

}
