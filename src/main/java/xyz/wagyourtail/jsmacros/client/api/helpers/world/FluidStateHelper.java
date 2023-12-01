package xyz.wagyourtail.jsmacros.client.api.helpers.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FluidStateHelper extends StateHelper<FluidState> {

    public FluidStateHelper(FluidState base) {
        super(base);
    }

    /**
     * @return the fluid's id.
     * @since 1.8.4
     */
    public String getId() {
        return Registry.FLUID.getId(base.getFluid()).toString();
    }

    /**
     * @return {@code true} if this fluid is still, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isStill() {
        return base.isStill();
    }

    /**
     * @return {@code true} if this fluid is empty (the default fluid state for non fluid blocks),
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /**
     * @return the height of this state.
     * @since 1.8.4
     */
    public float getHeight() {
        return base.getHeight();
    }

    /**
     * @return the level of this state.
     * @since 1.8.4
     */
    public int getLevel() {
        return base.getLevel();
    }

    /**
     * @return {@code true} if the fluid has some random tick logic (only used by lava to do the
     * fire spread), {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasRandomTicks() {
        return base.hasRandomTicks();
    }

    /**
     * @param pos the position in the world
     * @return the velocity that will be applied to entities at the given position.
     * @since 1.8.4
     */
    public Pos3D getVelocity(BlockPosHelper pos) {
        return new Pos3D(base.getVelocity(MinecraftClient.getInstance().world, pos.getRaw()));
    }

    /**
     * @return the block state of this fluid.
     * @since 1.8.4
     */
    public BlockStateHelper getBlockState() {
        return new BlockStateHelper(base.getBlockState());
    }

    /**
     * @return the blast resistance of this fluid.
     * @since 1.8.4
     */
    public float getBlastResistance() {
        return base.getBlastResistance();
    }

    @Override
    protected StateHelper<FluidState> create(FluidState base) {
        return new FluidStateHelper(base);
    }

    @Override
    public String toString() {
        return String.format("FluidStateHelper:{\"id\": \"%s\", \"properties\": %s}", getId(), toMap());
    }

}
