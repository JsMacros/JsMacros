package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class BlockDataHelper extends BaseHelper<BlockState> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final Block b;
    private final BlockPos bp;
    private final BlockEntity e;

    public BlockDataHelper(BlockState b, BlockEntity e, BlockPos bp) {
        super(b);
        this.b = b.getBlock();
        this.bp = bp;
        this.e = e;
    }

    /**
     * @return the {@code x} value of the block.
     * @since 1.1.7
     */
    public int getX() {
        return bp.getX();
    }

    /**
     * @return the {@code y} value of the block.
     * @since 1.1.7
     */
    public int getY() {
        return bp.getY();
    }

    /**
     * @return the {@code z} value of the block.
     * @since 1.1.7
     */
    public int getZ() {
        return bp.getZ();
    }

    /**
     * @return the item ID of the block.
     */
    @DocletReplaceReturn("BlockId")
    public String getId() {
        return Registries.BLOCK.getId(b).toString();
    }

    /**
     * @return the translated name of the block. (was string before 1.6.5)
     */
    public TextHelper getName() {
        return TextHelper.wrap(b.getName());
    }

    /**
     * @return
     * @since 1.5.1, used to be a {@link Map}&lt;{@link String}, {@link String}&gt;
     */
    @Nullable
    public NBTElementHelper.NBTCompoundHelper getNBT() {
        return e == null ? null : NBTElementHelper.wrapCompound(e.createNbt(RegistryHelper.WRAPPER_LOOKUP_UNLIMITED));
    }

    /**
     * @return
     * @since 1.6.5
     */
    public BlockStateHelper getBlockStateHelper() {
        return new BlockStateHelper(base);
    }

    /**
     * @return
     * @since 1.6.5
     * @deprecated use {@link #getBlock()} instead.
     */
    @Deprecated
    public BlockHelper getBlockHelper() {
        return getBlock();
    }

    /**
     * @return the block
     * @since 1.6.5
     */
    public BlockHelper getBlock() {
        return new BlockHelper(base.getBlock());
    }

    /**
     * @return block state data as a {@link Map}.
     * @since 1.1.7
     */
    public Map<String, String> getBlockState() {
        Map<String, String> map = new HashMap<>();
        for (Entry<Property<?>, Comparable<?>> e : base.getEntries().entrySet()) {
            map.put(e.getKey().getName(), Util.getValueAsString(e.getKey(), e.getValue()));
        }
        return map;
    }

    /**
     * @return the block pos.
     * @since 1.2.7
     */
    public BlockPosHelper getBlockPos() {
        return new BlockPosHelper(bp);
    }

    public Block getRawBlock() {
        return b;
    }

    public BlockState getRawBlockState() {
        return base;
    }

    public BlockEntity getRawBlockEntity() {
        return e;
    }

    @Override
    public String toString() {
        return String.format("BlockDataHelper:{\"x\": %d, \"y\": %d, \"z\": %d, \"id\": \"%s\"}", bp.getX(), bp.getY(), bp.getZ(), this.getId());
    }

}
