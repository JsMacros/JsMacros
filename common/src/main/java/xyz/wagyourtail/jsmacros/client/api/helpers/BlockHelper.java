package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockHelper extends BaseHelper<Block> {

    public BlockHelper(Block base) {
        super(base);
    }

    /**
     * @return the default state of the block.
     *
     * @since 1.6.5
     */
    public BlockStateHelper getDefaultState() {
        return new BlockStateHelper(base.getDefaultState());
    }

    /**
     * @return the default item stack of the block.
     *
     * @since 1.6.5
     */
    public ItemStackHelper getDefaultItemStack() {
        return new ItemStackHelper(base.asItem().getDefaultStack());
    }

    public boolean canMobSpawnInside() {
        return base.canMobSpawnInside();
    }

    /**
     * @return {@code true} if the block has dynamic bounds.
     *
     * @since 1.6.5
     */
    public boolean hasDynamicBounds() {
        return base.hasDynamicBounds();
    }

    /**
     * @return the blast resistance.
     *
     * @since 1.6.5
     */
    public float getBlastResistance() {
        return base.getBlastResistance();
    }

    /**
     * @return the jump velocity multiplier.
     *
     * @since 1.6.5
     */
    public float getJumpVelocityMultiplier() {
        return base.getJumpVelocityMultiplier();
    }

    /**
     * @return the slipperiness.
     *
     * @since 1.6.5
     */
    public float getSlipperiness() {
        return base.getSlipperiness();
    }

    /**
     * @return the hardness.
     *
     * @since 1.6.5
     */
    public float getHardness() {
        return base.getDefaultState().getHardness(null, null);
    }

    /**
     * @return the velocity multiplier.
     *
     * @since 1.6.5
     */
    public float getVelocityMultiplier() {
        return base.getVelocityMultiplier();
    }

    /**
     * @return all tags of the block as an {@link java.util.ArrayList ArrayList}.
     *
     * @since 1.6.5
     */
    public List<String> getTags() {
        return MinecraftClient.getInstance().getNetworkHandler().getTagManager().getBlocks().getTagsFor(base).stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return all possible block states of the block.
     *
     * @since 1.6.5
     */
    public List<BlockStateHelper> getStates() {
        return base.getStateManager().getStates().stream().map(BlockStateHelper::new).collect(Collectors.toList());
    }

    /**
     * @return the identifier of the block.
     *
     * @since 1.6.5
     */
    public String getId() {
        return Registry.BLOCK.getId(base).toString();
    }

    @Override
    public String toString() {
        return String.format("BlockDataHelper:{%s}", getId());
    }

}
