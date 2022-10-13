package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;
import xyz.wagyourtail.jsmacros.client.access.IChunkSection;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
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
     * @param world
     * @param blockFilter
     * @param stateFilter
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
     * @param centerX
     * @param centerZ
     * @param chunkrange
     * @return
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
     * @param range
     * @return
     */
    public List<PositionCommon.Pos3D> scanAroundPlayer(int range) {
        assert mc.player != null;
        return scanChunkRange(mc.player.getBlockPos().getX() >> 4, mc.player.getBlockPos().getZ() >> 4, range);
    }

    /**
     * Scans all chunks in the given range around the center chunk and returns a list of all block positions, for blocks matching the filter.
     * This will scan in a square with length 2*range + 1. So range = 0 for example will only scan the specified chunk,
     * while range = 1 will scan in a 3x3 area.
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
            PackedIntegerArray array = ((IPalettedContainer<?>) section.getContainer()).jsmacros_getData();
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

    private Map<String, Integer> getBlocksInChunksInternal(List<ChunkPos> chunkPositions, boolean ignoreState) {
        Object2IntOpenHashMap<String> result = new Object2IntOpenHashMap<>();

        getBestStream(chunkPositions).flatMap(pos -> {
            if (!world.getChunkManager().isChunkLoaded(pos.x, pos.z)) {
                return Stream.empty();
            }

            Object2IntOpenHashMap<BlockState> blocks = new Object2IntOpenHashMap<>();

            streamChunkSections(world.getChunk(pos.x, pos.z), (section, isInFilter) -> count(((IChunkSection) section).jsmacros_getContainer(), isInFilter, blocks::addTo));
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
        int size = 0;

        if (palette instanceof ArrayPalette) {
            size = ((ArrayPalette<BlockState>) palette).getSize();
        } else if (palette instanceof BiMapPalette) {
            size = ((BiMapPalette<BlockState>) palette).getIndexBits();
        }

        boolean[] isInFilter = new boolean[size];

        for (int i = 0; i < size; i++) {
            BlockState state = palette.getByIndex(i);
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
     * @return
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
            if (section == null || section.isEmpty()) {
                continue;
            }

            PalettedContainer<BlockState> sectionContainer = ((IChunkSection) section).jsmacros_getContainer();
            //this won't work if the PaletteStorage is of the type EmptyPaletteStorage
            if (((IPalettedContainer<?>) sectionContainer).jsmacros_getData() == null) {
                continue;
            }

            boolean[] isInFilter = getIncludedFilterIndices((Palette<BlockState>) ((IPalettedContainer<BlockState>) sectionContainer).jsmacros_getPaletteProvider());
            if (isInFilter.length == 0) {
                continue;
            }
            consumer.accept(section, isInFilter);
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
        long[] storage = array.getStorage();
        int arraySize = array.getSize();
        int elementBits = array.getElementBits();
        long maxValue = ((IPackedIntegerArray) array).jsmacros_getMaxValue();
        int storageLength = storage.length;

        if (storageLength != 0) {
            int lastStorageIdx = 0;
            long row = storage[0];
            long nextRow = storageLength > 1 ? storage[1] : 0L;

            for (int idx = 0; idx < arraySize; idx++) {
                int n = idx * elementBits;
                int storageIdx = n >> 6;
                int p = (idx + 1) * elementBits - 1 >> 6;
                int q = n ^ storageIdx << 6;
                if (storageIdx != lastStorageIdx) {
                    row = nextRow;
                    nextRow = storageIdx + 1 < storageLength ? storage[storageIdx + 1] : 0L;
                    lastStorageIdx = storageIdx;
                }
                if (storageIdx == p) {
                    if (isInFilter[(int) (row >>> q & maxValue)]) {
                        action.accept(counter);
                    } else {
                        if (isInFilter[(int) ((row >>> q | nextRow << (64 - q)) & maxValue)]) {
                            action.accept(counter);
                        }
                    }
                }
            }
        }
    }

    private static void count(PalettedContainer<BlockState> container, boolean[] isInFilter, PalettedContainer.CountConsumer<BlockState> counter) {
        IPalettedContainer<BlockState> data = ((IPalettedContainer<BlockState>) container);
        Palette<BlockState> palette = (Palette<BlockState>) data.jsmacros_getPaletteProvider();
        PackedIntegerArray storage = data.jsmacros_getData();

        int[] count = new int[((ArrayPalette<BlockState>) palette).getSize()];

        if (((ArrayPalette<BlockState>) palette).getSize() == 1) {
            counter.accept(palette.getByIndex(0), storage.getSize());
        } else {
            storage.forEach(key -> count[key]++);
            for (int idx = 0; idx < count.length; idx++) {
                if (isInFilter[idx]) {
                    counter.accept(palette.getByIndex(idx), count[idx]);
                }
            }
        }
    }

}
