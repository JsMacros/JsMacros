package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
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
@SuppressWarnings("unused")
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
     * @param world       the world to scan
     * @param blockFilter a filter method for the blocks
     * @param stateFilter a filter method for the block states
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
     * @param centerX    the x coordinate of the center chunk to scan around
     * @param centerZ    the z coordinate of the center chunk to scan around
     * @param chunkrange the range to scan around the center chunk
     * @return a list of all matching block positions.
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
     * @param chunkRange the range to scan around the center chunk
     * @return a list of all matching block positions.
     */
    public List<Pos3D> scanAroundPlayer(int chunkRange) {
        assert mc.player != null;
        return scanChunkRange(mc.player.getBlockPos().getX() >> 4, mc.player.getBlockPos().getZ() >> 4, chunkRange);
    }

    /**
     * Scans all chunks in the given range around the center chunk and returns a list of all block
     * positions, for blocks matching the filter. This will scan in a square with length 2*range +
     * 1. So range = 0 for example will only scan the specified chunk, while range = 1 will scan in
     * a 3x3 area.
     *
     * @param centerX    the x coordinate of the center chunk to scan around
     * @param centerZ    the z coordinate of the center chunk to scan around
     * @param chunkrange the range to scan around the center chunk
     * @return a list of all matching block positions.
     */
    public List<Pos3D> scanChunkRange(int centerX, int centerZ, int chunkrange) {
        assert world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }
        return scanChunksInternal(getChunkRange(centerX, centerZ, chunkrange));
    }

    private List<Pos3D> scanChunksInternal(List<ChunkPos> chunkPositions) {
        assert world != null;
        return getBestStream(chunkPositions).flatMap(this::scanChunkInternal).collect(Collectors.toList());
    }

    private Stream<Pos3D> scanChunkInternal(ChunkPos pos) {
        if (!world.isChunkLoaded(pos.x, pos.z)) {
            return Stream.empty();
        }

        long chunkX = (long) pos.x << 4;
        long chunkZ = (long) pos.z << 4;

        List<Pos3D> blocks = new ArrayList<>();

        streamChunkSections(world.getChunk(pos.x, pos.z), (section, isInFilter) -> {
            int yOffset = section.getYOffset();
            PackedIntegerArray array = ((IPalettedContainer<?>) section.getContainer()).jsmacros_getData();
            forEach(array, isInFilter, place -> blocks.add(new Pos3D(
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
     * @param chunkX      the x coordinate of the chunk to scan
     * @param chunkZ      the z coordinate of the chunk to scan
     * @param ignoreState whether multiple states should be combined to a single block
     * @return a map of all blocks inside the specified chunk and their respective count.
     */
    public Map<String, Integer> getBlocksInChunk(int chunkX, int chunkZ, boolean ignoreState) {
        return getBlocksInChunks(chunkX, chunkZ, 0, ignoreState);
    }

    /**
     * Gets the amount of all blocks matching the criteria inside a square around the player.
     *
     * @param centerX     the x coordinate of the center chunk to scan around
     * @param centerZ     the z coordinate of the center chunk to scan around
     * @param chunkRange  the range to scan around the center chunk
     * @param ignoreState whether multiple states should be combined to a single block
     * @return a map of all blocks inside the specified chunks and their respective count.
     */
    public Map<String, Integer> getBlocksInChunks(int centerX, int centerZ, int chunkRange, boolean ignoreState) {
        assert world != null;
        if (chunkRange < 0) {
            throw new IllegalArgumentException("chunkRange must be at least 0");
        }
        return getBlocksInChunksInternal(getChunkRange(centerX, centerZ, chunkRange), ignoreState);
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

    private boolean getFilterResult(BlockState state) {
        Boolean v;
        return (v = cachedFilterStates.get(state)) == null ? addCachedState(state) : v;
    }

    private boolean addCachedState(BlockState state) {
        boolean isInFilter = false;

        if (filter != null) {
            isInFilter = filter.apply(state);
        }

        cachedFilterStates.put(state, isInFilter);
        return isInFilter;
    }

    private boolean[] getIncludedFilterIndices(class_2748 palette) {
        boolean commonBlockFound = false;
        int size = palette.method_11775();

        boolean[] isInFilter = new boolean[size];

        for (int i = 0; i < size; i++) {
            BlockState state = palette.method_11776(i);
            if (getFilterResult(state)) {
                isInFilter[i] = true;
                commonBlockFound = true;
            } else {
                isInFilter[i] = false;
            }
        }

        if (!commonBlockFound) {
            return new boolean[0];
        }
        return isInFilter;
    }

    /**
     * Get the amount of cached block states. This will normally be around 200 - 400.
     *
     * @return the amount of cached block states.
     */
    public int getCachedAmount() {
        return cachedFilterStates.size();
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
