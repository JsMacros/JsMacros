package xyz.wagyourtail.jsmacros.client.api.helpers.block;

import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.Vec3d;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class FluidStateHelper extends StateHelper<FluidState> {

    public FluidStateHelper(FluidState base) {
        super(base);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isStill() {
        return base.isStill();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getHeight() {
        return base.getHeight();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getLevel() {
        return base.getLevel();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasRandomTicks() {
        return base.hasRandomTicks();
    }

    /**
     * @param pos
     * @return
     *
     * @since 1.9.0
     */
    public Vec3d getVelocity(BlockPosHelper pos) {
        return base.getVelocity(MinecraftClient.getInstance().world, pos.getRaw());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public BlockStateHelper getBlockState() {
        return new BlockStateHelper(base.getBlockState());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getBlastResistance() {
        return base.getBlastResistance();
    }

    @Override
    protected StateHelper<FluidState> create(FluidState base) {
        return new FluidStateHelper(base);
    }
}
