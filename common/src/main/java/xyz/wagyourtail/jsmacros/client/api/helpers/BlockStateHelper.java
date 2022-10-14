package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockStateHelper extends BaseHelper<IBlockState> {

    public BlockStateHelper(IBlockState base) {
        super(base);
    }

    /**
     * @return a map of the state properties with its identifier and value.
     *
     * @since 1.6.5
     */
    public Map<String, String> toMap() {
        return base.getProperties().stream().collect(Collectors.toMap(IProperty::getName, entry -> Objects.toString(base.get(entry), "null")));
    }

    /**
     * @return the block the state belongs to.
     *
     * @since 1.6.5
     */
    public BlockHelper getBlock() {
        return new BlockHelper(base.getBlock());
    }

    /**
     * @return the hardness.
     *
     * @since 1.6.5
     */
    public float getHardness() {
        return base.getBlock().getHarvestLevel(base);
    }

    /**
     * @return the luminance.
     *
     * @since 1.6.5
     */
    public int getLuminance() {
        return base.getBlock().getLightLevel();
    }

    /**
     * @return {@code true} if the state emits redstone power.
     *
     * @since 1.6.5
     */
    public boolean emitsRedstonePower() {
        return base.getBlock().emitsRedstonePower();
    }

    /**
     * @return {@code true} if the shape of the state is a cube.
     *
     * @since 1.6.5
     */
    public boolean exceedsCube() {
        return base.getBlock().isFullCube();
    }

    /**
     * @return {@code true} if the state is air.
     *
     * @since 1.6.5
     */
    public boolean isAir() {
        return base.getBlock().isAir(null, null);
    }

    /**
     * @return {@code true} if the state is opaque.
     *
     * @since 1.6.5
     */
    public boolean isOpaque() {
        return base.getBlock().getMaterial().isOpaque();
    }

    /**
     * @return {@code true} if a tool is required to mine the block.
     *
     * @since 1.6.5
     */
    public boolean isToolRequired() {
        return !base.getBlock().getMaterial().doesBlockMovement();
    }

    /**
     * @return {@code true} if the state has a block entity.
     *
     * @since 1.6.5
     */
    public boolean hasBlockEntity() {
        return base instanceof TileEntity;
    }

    /**
     * @return {@code true} if the state can be random ticked.
     *
     * @since 1.6.5
     */
    public boolean hasRandomTicks() {
        return base.getBlock().ticksRandomly();
    }

    /**
     * @return {@code true} if the state has a comparator output.
     *
     * @since 1.6.5
     */
    public boolean hasComparatorOutput() {
        return base.getBlock().hasComparatorOutput();
    }

    /**
     * @return the piston behaviour of the state.
     *
     * @since 1.6.5
     */
    public String getPistonBehaviour() {
        switch (base.getBlock().getPistonInteractionType()) {
            case 0:
                return "UNDEFINED";
            case 1:
                return "NO_PUSHING";
            case 2:
                return "IMMOVABLE";
            default:
                throw new IllegalStateException("Unexpected value: " + base.getBlock().getPistonInteractionType());
        }
    }

    /**
     * @return {@code true} if the state blocks light.
     *
     * @since 1.6.5
     */
    public boolean blocksLight() {
        return base.getBlock().getMaterial().isTransluscent();
    }

    /**
     * @return {@code true} if the state blocks the movement of entities.
     *
     * @since 1.6.5
     */
    public boolean blocksMovement() {
        return base.getBlock().getMaterial().doesBlockMovement();
    }

    /**
     * @return {@code true} if the state is burnable.
     *
     * @since 1.6.5
     */
    public boolean isBurnable() {
        return base.getBlock().getMaterial().isBurnable();
    }

    /**
     * @return {@code true} if the state is a liquid.
     *
     * @since 1.6.5* @since 1.6.5
     */
    public boolean isLiquid() {
        return base.getBlock().getMaterial().isFluid();
    }

    /**
     * @return {@code true} if the state is solid.
     *
     * @since 1.6.5* @since 1.6.5
     */
    public boolean isSolid() {
        return base.getBlock().isFullBlock();
    }

    /**
     * This will return true for blocks like air and grass, that can be replaced without breaking
     * them first.
     *
     * @return {@code true} if the state can be replaced.
     *
     * @since 1.6.5
     */
    public boolean isReplaceable() {
        return base.getBlock().getMaterial().isReplaceable();
    }

    /**
     * @param pos
     * @param entity
     * @return {@code true} if the entity can spawn on this block state at the given position in the
     *         current world.
     *
     * @since 1.6.5
     */
    public boolean allowsSpawning(BlockPosHelper pos, String entity) {
        return false;
    }

    /**
     * @param pos
     * @return {@code true} if an entity can suffocate in this block state at the given position in
     *         the current world.
     *
     * @since 1.6.5
     */
    public boolean shouldSuffocate(BlockPosHelper pos) {
        return false;
    }

    @Override
    public String toString() {
        return String.format("BlockStateHelper:{%s, %s}", getBlock().getId(), toMap());
    }

}
