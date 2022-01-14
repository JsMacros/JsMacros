package xyz.wagyourtail.jsmacros.client.api.classes;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.impl.JSScriptContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class WorldScanner {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final World world;
    private final Map<BlockState, Boolean> cachedFilterStates;
    private final Function<BlockState, Boolean> filter;
    
    private boolean useParallelStream = true;

    public WorldScanner(World world, Function<Block, Boolean> blockFilter, Function<BlockState, Boolean> stateFilter) {
        this.world = world;

        checkParallelStreamAllowed(blockFilter);
        checkParallelStreamAllowed(stateFilter);
        
        this.filter = combineFilter(blockFilter, stateFilter);
        cachedFilterStates = new ConcurrentHashMap<>();
    }

    private void checkParallelStreamAllowed(Function<?, Boolean> filter) {
        if (filter instanceof MethodWrapper<?, ?, ?, ?> wrapper) {
            if (wrapper.getCtx() instanceof JSScriptContext) {
                useParallelStream = false;
            }
        }
    }

    private <V> Stream<V> getBestStream(List<V> list) {
        if (useParallelStream) {
            return list.stream().parallel();
        } else {
            return list.stream();
        }
    }
    
    private static Function<BlockState, Boolean> combineFilter(Function<Block, Boolean> blockFilter, Function<BlockState, Boolean> stateFilter) {
        if (blockFilter != null) {
            if (stateFilter != null) {
                return state -> blockFilter.apply(state.getBlock()) && stateFilter.apply(state);
            } else {
                return state -> blockFilter.apply(state.getBlock());
            }
        } else {
            return stateFilter;
        }
    }

    public List<ChunkPos> getChunkRange(int centerX, int centerZ, int chunkrange) {
        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = centerX - chunkrange; x <= centerX + chunkrange; x++) {
            for (int z = centerZ - chunkrange; z <= centerZ + chunkrange; z++) {
                chunks.add(new ChunkPos(x, z));
            }
        }
        return chunks;
    }

    public List<PositionCommon.Pos3D> scanAroundPlayer(int range) {
        assert mc.player != null;
        return scanChunkRange(mc.player.getChunkPos().x, mc.player.getChunkPos().z, range);
    }

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

        for (ChunkSection section : world.getChunk(pos.x, pos.z).getSectionArray()) {
            if (section.isEmpty()) {
                continue;
            }
            PalettedContainer<BlockState> sectionContainer = section.getBlockStateContainer();
            Palette<BlockState> palette = ((IPalettedContainer<BlockState>) sectionContainer).jsmacros_getData().jsmacros_getPalette();

            //this won't work if the PaletteStorage is of the type EmptyPaletteStorage
            if (!(((IPalettedContainer<?>) sectionContainer).jsmacros_getData().jsmacros_getStorage() instanceof PackedIntegerArray array)) {
                continue;
            }

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
                continue;
            }
            
            int yOffset = section.getYOffset();

            forEach(array, isInFilter, place -> blocks.add(new PositionCommon.Pos3D(
                    chunkX + ((place & 255) & 15),
                    yOffset + (place >> 8),
                    chunkZ + ((place & 255) >> 4)
            )));
        }
        return blocks.stream();
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

    public Map<String, Integer> getBlocksInChunk(int chunkX, int chunkZ, boolean ignoreState) {
        return getBlocksInChunks(chunkX, chunkZ, 0, ignoreState);
    }

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

            for (ChunkSection section : world.getChunk(pos.x, pos.z).getSectionArray()) {
                if (section.isEmpty()) {
                    continue;
                }
                section.getBlockStateContainer().count(blocks::addTo);
            }
            
            return blocks.object2IntEntrySet().stream();
        }).forEach(blockStateEntry -> {
            BlockState state = blockStateEntry.getKey();
            if (getFilterResult(state)) {
                result.addTo(ignoreState ? state.getBlock().toString() : state.toString(), blockStateEntry.getIntValue());
            }
        });
        return result;
    }

    public int getCachedAmount() {
        return cachedFilterStates.size();
    }
    
}
