package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;

/**
 * @author Etheradon
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class BlockStateHelper extends StateHelper<BlockState> {

    public BlockStateHelper(BlockState base) {
        super(base);
    }

    /**
     * @return the block the state belongs to.
     * @since 1.6.5
     */
    public BlockHelper getBlock() {
        return new BlockHelper(base.getBlock());
    }

    /**
     * @return the block's id.
     * @since 1.8.4
     */
    @DocletReplaceReturn("BlockId")
    public String getId() {
        return Registries.BLOCK.getId(base.getBlock()).toString();
    }

    /**
     * @return the fluid state of this block state.
     * @since 1.8.4
     */
    public FluidStateHelper getFluidState() {
        return new FluidStateHelper(base.getFluidState());
    }

    /**
     * @return the hardness.
     * @since 1.6.5
     */
    public float getHardness() {
        return base.getHardness(null, null);
    }

    /**
     * @return the luminance.
     * @since 1.6.5
     */
    public int getLuminance() {
        return base.getLuminance();
    }

    /**
     * @return {@code true} if the state emits redstone power.
     * @since 1.6.5
     */
    public boolean emitsRedstonePower() {
        return base.emitsRedstonePower();
    }

    /**
     * @return {@code true} if the shape of the state is a cube.
     * @since 1.6.5
     */
    public boolean exceedsCube() {
        return base.exceedsCube();
    }

    /**
     * @return {@code true} if the state is air.
     * @since 1.6.5
     */
    public boolean isAir() {
        return base.isAir();
    }

    /**
     * @return {@code true} if the state is opaque.
     * @since 1.6.5
     */
    public boolean isOpaque() {
        return base.isOpaque();
    }

    /**
     * @return {@code true} if a tool is required to mine the block.
     * @since 1.6.5
     */
    public boolean isToolRequired() {
        return base.isToolRequired();
    }

    /**
     * @return {@code true} if the state has a block entity.
     * @since 1.6.5
     */
    public boolean hasBlockEntity() {
        return base.hasBlockEntity();
    }

    /**
     * @return {@code true} if the state can be random ticked.
     * @since 1.6.5
     */
    public boolean hasRandomTicks() {
        return base.hasRandomTicks();
    }

    /**
     * @return {@code true} if the state has a comparator output.
     * @since 1.6.5
     */
    public boolean hasComparatorOutput() {
        return base.hasComparatorOutput();
    }

    /**
     * @return the piston behaviour of the state.
     * @since 1.6.5
     */
    @DocletReplaceReturn("PistonBehaviour")
    public String getPistonBehaviour() {
        switch (base.getPistonBehavior()) {
            case NORMAL:
                return "NORMAL";
            case BLOCK:
                return "BLOCK";
            case PUSH_ONLY:
                return "PUSH_ONLY";
            case DESTROY:
                return "DESTROY";
            case IGNORE:
                return "IGNORE";
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * @return {@code true} if the state blocks the movement of entities.
     * @since 1.6.5
     */
    public boolean blocksMovement() {
        return base.blocksMovement();
    }

    /**
     * @return {@code true} if the state is burnable.
     * @since 1.6.5
     */
    public boolean isBurnable() {
        return base.isBurnable();
    }

    /**
     * @return {@code true} if the state is a liquid.
     * @since 1.6.5
     */
    public boolean isLiquid() {
        return base.isLiquid();
    }

    /**
     * @return {@code true} if the state is solid.
     * @since 1.6.5
     */
    public boolean isSolid() {
        return base.isSolid();
    }

    /**
     * This will return true for blocks like air and grass, that can be replaced without breaking
     * them first.
     *
     * @return {@code true} if the state can be replaced.
     * @since 1.6.5
     */
    public boolean isReplaceable() {
        return base.isReplaceable();
    }

    /**
     * @param pos    the position of the block to check
     * @param entity the entity type to check
     * @return {@code true} if the entity can spawn on this block state at the given position in the
     * current world.
     * @since 1.6.5
     */
    @DocletReplaceParams("pos: BlockPosHelper, entity: CanOmitNamespace<EntityId>")
    public boolean allowsSpawning(BlockPosHelper pos, String entity) {
        return base.allowsSpawning(MinecraftClient.getInstance().world, pos.getRaw(), Registries.ENTITY_TYPE.get(RegistryHelper.parseIdentifier(entity)));
    }

    /**
     * @param pos the position of the block to check
     * @return {@code true} if an entity can suffocate in this block state at the given position in
     * the current world.
     * @since 1.6.5
     */
    public boolean shouldSuffocate(BlockPosHelper pos) {
        return base.shouldSuffocate(MinecraftClient.getInstance().world, pos.getRaw());
    }

    /**
     * @return an {@link UniversalBlockStateHelper} to access all properties of this block state.
     * @since 1.8.4
     */
    public UniversalBlockStateHelper getUniversal() {
        return new UniversalBlockStateHelper(base);
    }

    @Override
    protected StateHelper<BlockState> create(BlockState base) {
        return new BlockStateHelper(base);
    }

    @Override
    public String toString() {
        return String.format("BlockStateHelper:{\"id\": \"%s\", \"properties\": %s}", getId(), toMap());
    }

}
