package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import xyz.wagyourtail.jsmacros.client.access.IExtenedBlockStorage;
import xyz.wagyourtail.jsmacros.client.access.IObjectIntIdentityMap;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class to scan the world for certain blocks. The results of the filters are cached, so it's a
 * good idea to reuse an instance of this if possible. The scanner can either return a list of all
 * block positions or a list of blocks and their respective count for every block / state matching
 * the filters criteria.
 *
 * @author Etheradon
 * @since 1.6.5
 */
public class WorldScanner {

    private static final Minecraft mc = Minecraft.getInstance();

    private final World world;

    private final boolean[] cachedFilterStates;
    private final Function<IBlockState, Boolean> filter;

    private final boolean useParallelStream;

    /**
     * Creates a new World scanner with for the given world. It accepts two boolean functions, one
     * for {@link BlockHelper} and the other for {@link BlockStateHelper}.
     *
     * @param world
     * @param blockFilter
     * @param stateFilter
     */
    public WorldScanner(World world, Function<BlockHelper, Boolean> blockFilter, Function<BlockStateHelper, Boolean> stateFilter) {
        this.world = world;
        this.useParallelStream = isParallelStreamAllowed(blockFilter) && isParallelStreamAllowed(stateFilter);
        this.filter = combineFilter(blockFilter, stateFilter);
        cachedFilterStates = new boolean[((IObjectIntIdentityMap) Block.BLOCK_STATES).jsmacros_getSize()];
        initializeFilter();
    }

    private void initializeFilter() {
        int stateCount = ((IObjectIntIdentityMap) Block.BLOCK_STATES).jsmacros_getSize();
        for (int i = 0; i < stateCount; i++) {
            IBlockState state = Block.BLOCK_STATES.fromId(i);
            if (state != null) {
                cachedFilterStates[i] = filter.apply(state);
            }
        }
    }

    /**
     * Gets a list of all chunks in the given range around the center chunk.
     *
     * @param centerX
     * @param centerZ
     * @param chunkrange
     * @return
     */
    public List<ChunkCoordIntPair> getChunkRange(int centerX, int centerZ, int chunkrange) {
        List<ChunkCoordIntPair> chunks = new ArrayList<>();
        for (int x = centerX - chunkrange; x <= centerX + chunkrange; x++) {
            for (int z = centerZ - chunkrange; z <= centerZ + chunkrange; z++) {
                chunks.add(new ChunkCoordIntPair(x, z));
            }
        }
        return chunks;
    }

    /**
     * Scans all chunks in the given range around the player and returns a list of all block
     * positions, for blocks matching the filter. This will scan in a square with length 2*range +
     * 1. So range = 0 for example will only scan the chunk the player is standing in, while range =
     * 1 will scan in a 3x3 area.
     *
     * @param range
     * @return
     */
    public List<PositionCommon.Pos3D> scanAroundPlayer(int range) {
        assert mc.player != null;
        return scanChunkRange(mc.player.getBlockPos().getX() >> 4, mc.player.getBlockPos().getZ() >> 4, range);
    }

    /**
     * Scans all chunks in the given range around the center chunk and returns a list of all block
     * positions, for blocks matching the filter. This will scan in a square with length 2*range +
     * 1. So range = 0 for example will only scan the specified chunk, while range = 1 will scan in
     * a 3x3 area.
     *
     * @param centerX
     * @param centerZ
     * @param chunkrange
     * @return the list
     */
    public List<PositionCommon.Pos3D> scanChunkRange(int centerX, int centerZ, int chunkrange) {
        assert world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }
        return scanChunksInternal(getChunkRange(centerX, centerZ, chunkrange));
    }

    private List<PositionCommon.Pos3D> scanChunksInternal(List<ChunkCoordIntPair> chunkPositions) {
        assert world != null;
        return getBestStream(chunkPositions).flatMap(this::scanChunkInternal).collect(Collectors.toList());
    }

    private Stream<PositionCommon.Pos3D> scanChunkInternal(ChunkCoordIntPair pos) {
        if (!world.getChunkProvider().chunkExists(pos.x, pos.z)) {
            return Stream.empty();
        }

        long chunkX = (long) pos.x << 4;
        long chunkZ = (long) pos.z << 4;

        List<PositionCommon.Pos3D> blocks = new ArrayList<>();

        streamChunkSections(world.getChunk(pos.x, pos.z), section -> {
            int yOffset = section.func_76662_d();
            char[] array = section.getBlockStates();
            forEach(array, ((IExtenedBlockStorage) section).jsmacros_getNonEmptyBlockCount(), place -> blocks.add(new PositionCommon.Pos3D(
                    chunkX + ((place & 255) & 15),
                    yOffset + (place >> 8),
                    chunkZ + ((place & 255) >> 4)
            )));
        });
        return blocks.stream();
    }

    /**
     * Gets the amount of all blocks matching the criteria inside the chunk.
     *
     * @param chunkX
     * @param chunkZ
     * @param ignoreState whether multiple states should be combined to a single block
     * @return
     */
    public Map<String, Integer> getBlocksInChunk(int chunkX, int chunkZ, boolean ignoreState) {
        return getBlocksInChunks(chunkX, chunkZ, 0, ignoreState);
    }

    /**
     * Gets the amount of all blocks matching the criteria inside square around the center chunk
     * with radius chunkrange/2.
     *
     * @param centerX
     * @param centerZ
     * @param chunkrange
     * @param ignoreState whether multiple states should be combined to a single block
     * @return
     */
    public Map<String, Integer> getBlocksInChunks(int centerX, int centerZ, int chunkrange, boolean ignoreState) {
        assert world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }
        return getBlocksInChunksInternal(getChunkRange(centerX, centerZ, chunkrange), ignoreState);
    }

    private Map<String, Integer> getBlocksInChunksInternal(List<ChunkCoordIntPair> chunkPositions, boolean ignoreState) {
        Map<String, Integer> result = new HashMap<>();

        getBestStream(chunkPositions).flatMap(pos -> {
            if (!world.getChunkProvider().chunkExists(pos.x, pos.z)) {
                return Stream.empty();
            }
            Map<IBlockState, Integer> blocks = new HashMap<>();

            streamChunkSections(world.getChunk(pos.x, pos.z), section -> count(section, ((IExtenedBlockStorage) section).jsmacros_getNonEmptyBlockCount(), blocks));
            return blocks.entrySet().stream();
        }).forEach(blockStateEntry -> {
            IBlockState state = blockStateEntry.getKey();
            String key = ignoreState ? state.getBlock().toString() : state.toString();
            if (result.containsKey(key)) {
                result.put(key, result.get(key) + blockStateEntry.getValue());
            } else {
                result.put(key, blockStateEntry.getValue());
            }
        });
        return result;
    }

    private <V> Stream<V> getBestStream(List<V> list) {
        if (useParallelStream) {
            return list.stream().parallel();
        } else {
            return list.stream();
        }
    }

    private void streamChunkSections(Chunk chunk, Consumer<ExtendedBlockStorage> consumer) {
        for (ExtendedBlockStorage section : chunk.getBlockStorage()) {
            if (section == null || section.func_76663_a()) {
                continue;
            }
            consumer.accept(section);
        }
    }

    private static boolean isParallelStreamAllowed(Function<?, Boolean> filter) {
        if (filter instanceof MethodWrapper<?, ?, ?, ?>) {
            MethodWrapper<?, ?, ?, ?> wrapper = (MethodWrapper<?, ?, ?, ?>) filter;
            if (!wrapper.getCtx().isMultiThreaded()) {
                return false;
            }
        }
        return true;
    }

    private static Function<IBlockState, Boolean> combineFilter(Function<BlockHelper, Boolean> blockFilter, Function<BlockStateHelper, Boolean> stateFilter) {
        if (blockFilter != null) {
            if (stateFilter != null) {
                return state -> blockFilter.apply(new BlockHelper(state.getBlock())) && stateFilter.apply(new BlockStateHelper(state));
            } else {
                return state -> blockFilter.apply(new BlockHelper(state.getBlock()));
            }
        } else if (stateFilter != null) {
            return state -> stateFilter.apply(new BlockStateHelper(state));
        } else {
            return null;
        }
    }

    private void forEach(char[] storage, int maxBlockCount, IntConsumer action) {
        int counter = 0;
        int arraySize = 4096;

        for (int idx = 0; idx < arraySize && counter < maxBlockCount; idx++) {
            if (cachedFilterStates[storage[idx]]) {
                action.accept(idx);
            }
            counter++;
        }

    }

    private static void count(ExtendedBlockStorage section, int maxBlockCount, Map<IBlockState, Integer> blocks) {
        int[] blockLookup = new int[((IObjectIntIdentityMap) Block.BLOCK_STATES).jsmacros_getSize()];

        int counter = 0;
        int arraySize = 4096;

        for (int idx = 0; idx < arraySize && counter < maxBlockCount; idx++) {
            blockLookup[section.getBlockStates()[idx]]++;
            counter++;
        }

        for (int id = 0; id < blockLookup.length; id++) {
            if (blockLookup[id] > 0) {
                blocks.put(Block.BLOCK_STATES.fromId(id), blockLookup[id]);
            }
        }

    }

}
