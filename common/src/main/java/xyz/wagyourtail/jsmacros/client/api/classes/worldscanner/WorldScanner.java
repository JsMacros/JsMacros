package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainerData;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class to scan the world for certain blocks. The results of the filters are cached, 
 * so it's a good idea to reuse an instance of this if possible. 
 * The scanner can either return a list of all block positions or
 * a list of blocks and their respective count for every block / state matching the filters criteria.
 * 
 * @author Etheradon
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class WorldScanner {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final World world;
    private final Map<BlockState, Boolean> cachedFilterStates;

    private final Function<BlockState, Boolean> filter;

    private final boolean useParallelStream;

    /**
     * Creates a new World scanner with for the given world. It accepts two boolean functions, 
     * one for {@link BlockHelper} and the other for {@link BlockStateHelper}.
     *
     * @param world       the world to scan
     * @param blockFilter a filter method for the blocks
     * @param stateFilter a filter method for the block states
     */
    public WorldScanner(World world, Function<BlockHelper, Boolean> blockFilter, Function<BlockStateHelper, Boolean> stateFilter) {
        this.world = world;
        this.useParallelStream = isParallelStreamAllowed(blockFilter) && isParallelStreamAllowed(stateFilter);
        this.filter = combineFilter(blockFilter, stateFilter);
        cachedFilterStates = new ConcurrentHashMap<>();
    }

    /**
     * Gets a list of all chunks in the given range around the center chunk.
     *
     * @param centerX    the x coordinate of the center chunk to scan around
     * @param centerZ    the z coordinate of the center chunk to scan around
     * @param chunkrange the range to scan around the center chunk
     * @return a list of all matching block positions.
     */
    public List<ChunkPos> getChunkRange(int centerX, int centerZ, int chunkrange) {
        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = centerX - chunkrange; x <= centerX + chunkrange; x++) {
            for (int z = centerZ - chunkrange; z <= centerZ + chunkrange; z++) {
                chunks.add(new ChunkPos(x, z));
            }
        }
        return chunks;
    }

    /**
     * Scans all chunks in the given range around the player and returns a list of all block positions, for blocks matching the filter.
     * This will scan in a square with length 2*range + 1. So range = 0 for example will only scan the chunk the player
     * is standing in, while range = 1 will scan in a 3x3 area.
     *
     * @param chunkRange the range to scan around the center chunk
     * @return a list of all matching block positions.
     */
    public List<PositionCommon.Pos3D> scanAroundPlayer(int chunkRange) {
        assert mc.player != null;
        return scanChunkRange(mc.player.getChunkPos().x, mc.player.getChunkPos().z, chunkRange);
    }

    /**
     * Scans all chunks in the given range around the center chunk and returns a list of all block positions, for blocks matching the filter.
     * This will scan in a square with length 2*range + 1. So range = 0 for example will only scan the specified chunk,
     * while range = 1 will scan in a 3x3 area.
     *
     * @param centerX    the x coordinate of the center chunk to scan around
     * @param centerZ    the z coordinate of the center chunk to scan around
     * @param chunkrange the range to scan around the center chunk
     * @return a list of all matching block positions.
     */
    public List<PositionCommon.Pos3D> scanChunkRange(int centerX, int centerZ, int chunkrange) {
        assert world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }
        return scanChunksInternal(getChunkRange(centerX, centerZ, chunkrange));
    }

    private List<PositionCommon.Pos3D> scanChunksInternal(List<ChunkPos> chunkPositions) {
        assert world != null;
        return getBestStream(chunkPositions).flatMap(this::scanChunkInternal).collect(Collectors.toList());
    }

    private Stream<PositionCommon.Pos3D> scanChunkInternal(ChunkPos pos) {
        if (!world.isChunkLoaded(pos.x, pos.z)) {
            return Stream.empty();
        }

        long chunkX = (long) pos.x << 4;
        long chunkZ = (long) pos.z << 4;

        List<PositionCommon.Pos3D> blocks = new ArrayList<>();

        streamChunkSections(world.getChunk(pos.x, pos.z), (section, isInFilter) -> {
            int yOffset = section.getYOffset();
            PackedIntegerArray array = (PackedIntegerArray) ((IPalettedContainer<?>) section.getBlockStateContainer()).jsmacros_getData().jsmacros_getStorage();
            forEach(array, isInFilter, place -> blocks.add(new PositionCommon.Pos3D(
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

    private Map<String, Integer> getBlocksInChunksInternal(List<ChunkPos> chunkPositions, boolean ignoreState) {
        Object2IntOpenHashMap<String> result = new Object2IntOpenHashMap<>();

        getBestStream(chunkPositions).flatMap(pos -> {
            if (!world.getChunkManager().isChunkLoaded(pos.x, pos.z)) {
                return Stream.empty();
            }

            Object2IntOpenHashMap<BlockState> blocks = new Object2IntOpenHashMap<>();

            streamChunkSections(world.getChunk(pos.x, pos.z), (section, isInFilter) -> count(section.getBlockStateContainer(), isInFilter, blocks::addTo));
            return blocks.object2IntEntrySet().stream();
        }).forEach(blockStateEntry -> {
            BlockState state = blockStateEntry.getKey();
            result.addTo(ignoreState ? state.getBlock().toString() : state.toString(), blockStateEntry.getIntValue());
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

    private boolean[] getIncludedFilterIndices(Palette<BlockState> palette) {
        boolean commonBlockFound = false;
        boolean[] isInFilter = new boolean[palette.getSize()];

        for (int i = 0; i < palette.getSize(); i++) {
            BlockState state = palette.get(i);
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
    
    private void streamChunkSections(Chunk chunk, BiConsumer<ChunkSection, boolean[]> consumer) {
        for (ChunkSection section : chunk.getSectionArray()) {
            if (section.isEmpty()) {
                continue;
            }

            PalettedContainer<BlockState> sectionContainer = section.getBlockStateContainer();
            //this won't work if the PaletteStorage is of the type EmptyPaletteStorage
            if (!(((IPalettedContainer<?>) sectionContainer).jsmacros_getData().jsmacros_getStorage() instanceof PackedIntegerArray)) {
                continue;
            }

            boolean[] isInFilter = getIncludedFilterIndices(((IPalettedContainer<BlockState>) sectionContainer).jsmacros_getData().jsmacros_getPalette());
            if (isInFilter.length == 0) {
                continue;
            }
            consumer.accept(section, isInFilter);
        }
    }

    private static boolean isParallelStreamAllowed(Function<?, Boolean> filter) {
        if (filter instanceof MethodWrapper<?, ?, ?, ?> wrapper) {
            if (!wrapper.getCtx().isMultiThreaded()) {
                return false;
            }
        }
        return true;
    }

    private static Function<BlockState, Boolean> combineFilter(Function<BlockHelper, Boolean> blockFilter, Function<BlockStateHelper, Boolean> stateFilter) {
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
    
    private static void forEach(PackedIntegerArray array, boolean[] isInFilter, IntConsumer action) {
        int counter = 0;

        int elementsPerLong = ((IPackedIntegerArray) array).jsmacros_getElementsPerLong();
        long maxValue = ((IPackedIntegerArray) array).jsmacros_getMaxValue();
        int elementBits = array.getElementBits();
        int size = array.getSize();

        for (long datum : array.getData()) {
            long row = datum;
            if (row == 0) {
                counter += elementsPerLong;
                continue;
            }
            for (int idx = 0; idx < elementsPerLong; idx++) {
                if (isInFilter[(int) (row & maxValue)]) {
                    action.accept(counter);
                }

                row >>= elementBits;
                counter++;
                if (counter >= size) {
                    return;
                }
            }
        }
    }
    
    private static void count(PalettedContainer<BlockState> container, boolean[] isInFilter, PalettedContainer.Counter<BlockState> counter) {
        IPalettedContainerData<BlockState> data = ((IPalettedContainer<BlockState>) container).jsmacros_getData();
        Palette<BlockState> palette = data.jsmacros_getPalette();
        PaletteStorage storage = data.jsmacros_getStorage();

        int[] count = new int[palette.getSize()];

        if (palette.getSize() == 1) {
            counter.accept(palette.get(0), storage.getSize());
        } else {
            storage.forEach(key -> count[key]++);
            for (int idx = 0; idx < count.length; idx++) {
                if (isInFilter[idx]) {
                    counter.accept(palette.get(idx), count[idx]);
                }
            }
        }
    }

}
