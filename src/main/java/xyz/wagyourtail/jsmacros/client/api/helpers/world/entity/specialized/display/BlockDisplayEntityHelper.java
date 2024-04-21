package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.display;

import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.DisplayEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class BlockDisplayEntityHelper extends DisplayEntityHelper<DisplayEntity.BlockDisplayEntity> {

    public BlockDisplayEntityHelper(DisplayEntity.BlockDisplayEntity base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public BlockStateHelper getBlockState() {
        BlockState data = base.getBlockState();
        if (data == null) return null;
        return new BlockStateHelper(data);
    }

}
