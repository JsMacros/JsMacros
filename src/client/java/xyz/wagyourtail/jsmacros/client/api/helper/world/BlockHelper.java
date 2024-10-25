package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class BlockHelper extends BaseHelper<Block> {

    public BlockHelper(Block base) {
        super(base);
    }

    /**
     * @return the default state of the block.
     * @since 1.6.5
     */
    public BlockStateHelper getDefaultState() {
        return new BlockStateHelper(base.getDefaultState());
    }

    /**
     * @return the default item stack of the block.
     * @since 1.6.5
     */
    public ItemStackHelper getDefaultItemStack() {
        return new ItemStackHelper(base.asItem().getDefaultStack());
    }

    public boolean canMobSpawnInside() {
        return base.canMobSpawnInside(base.getDefaultState());
    }

    /**
     * @return {@code true} if the block has dynamic bounds.
     * @since 1.6.5
     */
    public boolean hasDynamicBounds() {
        return base.hasDynamicBounds();
    }

    /**
     * @return the blast resistance.
     * @since 1.6.5
     */
    public float getBlastResistance() {
        return base.getBlastResistance();
    }

    /**
     * @return the jump velocity multiplier.
     * @since 1.6.5
     */
    public float getJumpVelocityMultiplier() {
        return base.getJumpVelocityMultiplier();
    }

    /**
     * @return the slipperiness.
     * @since 1.6.5
     */
    public float getSlipperiness() {
        return base.getSlipperiness();
    }

    /**
     * @return the hardness.
     * @since 1.6.5
     */
    public float getHardness() {
        return base.getHardness();
    }

    /**
     * @return the velocity multiplier.
     * @since 1.6.5
     */
    public float getVelocityMultiplier() {
        return base.getVelocityMultiplier();
    }

    /**
     * @return all tags of the block as an {@link java.util.ArrayList ArrayList}.
     * @since 1.6.5
     */
    @DocletReplaceReturn("JavaList<BlockTag>")
    public List<String> getTags() {
        return base.getRegistryEntry().streamTags().map(t -> t.id().toString()).collect(Collectors.toList());
    }

    /**
     * @return all possible block states of the block.
     * @since 1.6.5
     */
    public List<BlockStateHelper> getStates() {
        return base.getStateManager().getStates().stream().map(BlockStateHelper::new).collect(Collectors.toList());
    }

    /**
     * @return the identifier of the block.
     * @since 1.6.5
     */
    @DocletReplaceReturn("BlockId")
    public String getId() {
        return Registries.BLOCK.getId(base).toString();
    }

    /**
     * @return the name of the block.
     * @since 1.8.4
     */
    public TextHelper getName() {
        return TextHelper.wrap(base.getName());
    }

    @Override
    public String toString() {
        return String.format("BlockHelper:{\"id\": \"%s\"}", getId());
    }

}
