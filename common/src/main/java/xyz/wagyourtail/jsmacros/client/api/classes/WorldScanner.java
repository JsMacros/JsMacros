package xyz.wagyourtail.jsmacros.client.api.classes;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldScanner {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final World world;

    private final Map<BlockState, Boolean> cachedFilterStates;

    private final Function<BlockState, Boolean> filter;

    public WorldScanner(World world, Function<Block, Boolean> blockFilter, Function<BlockState, Boolean> stateFilter) {
        this.world = world;
        blockFilter = state -> state.toString().contains("ore");
        this.filter = combineFilter(blockFilter, stateFilter);
        cachedFilterStates = new ConcurrentHashMap<>();
    }

    public PositionCommon.Pos2D getPlayerChunk() {
        ChunkPos playerChunk = mc.player.getChunkPos();
        return new PositionCommon.Pos2D(playerChunk.x, playerChunk.z);
    }

    public List<PositionCommon.Pos3D> scanAroundPlayer(int range) {
        assert mc.player != null;
        return findBlocksMatchingInternal(mc.player.getChunkPos().x, mc.player.getChunkPos().z, range);
    }

    // TODO: Different filtering methods
    private List<PositionCommon.Pos3D> findBlocksMatchingInternal(int centerX, int centerZ, int chunkrange) {
        assert world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }

        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = centerX - chunkrange; x <= centerX + chunkrange; x++) {
            for (int z = centerZ - chunkrange; z <= centerZ + chunkrange; z++) {
                if (world.isChunkLoaded(x, z)) {
                    chunks.add(new ChunkPos(x, z));
                }
            }
        }

        return findBlocksMatchingInternal(chunks);
    }

    private List<PositionCommon.Pos3D> findBlocksMatchingInternal(List<ChunkPos> pos) {
        assert world != null;

        return pos.stream().parallel().flatMap(c -> {
            if (!world.isChunkLoaded(c.x, c.z)) {
                return Stream.empty();
            }

            long chunkX = (long) c.x << 4;
            long chunkZ = (long) c.z << 4;

            List<PositionCommon.Pos3D> blocks = new ArrayList<>();

            for (ChunkSection section : world.getChunk(c.x, c.z).getSectionArray()) {
                if (section.isEmpty()) {
                    continue;
                }

                PalettedContainer<BlockState> sectionContainer = section.getBlockStateContainer();
                Palette<BlockState> palette = ((IPalettedContainer<BlockState>) sectionContainer).getData().getPalette();

                //this won't work if the PaletteStorage is of the type EmptyPaletteStorage
                if (!(((IPalettedContainer<?>) sectionContainer).getData().getStorage() instanceof PackedIntegerArray array)) {
                    continue;
                }

                boolean commonBlockFound = false;
                boolean[] isInFilter = new boolean[palette.getSize()];

                for (int i = 0; i < palette.getSize(); i++) {
                    BlockState state = palette.get(i);
                    if (cachedFilterStates.getOrDefault(state, addCachedState(state, filter))) {
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
        }).collect(Collectors.toList());
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

    private boolean addCachedState(BlockState state, Function<BlockState, Boolean> filter) {
        boolean isInFilter = false;

        if (filter != null) {
            isInFilter = filter.apply(state);
        }

        cachedFilterStates.put(state, isInFilter);
        return isInFilter;
    }

    private static void forEach(PackedIntegerArray array, boolean[] isInFilter, IntConsumer action) {
        int counter = 0;

        int elementsPerLong = ((IPackedIntegerArray) array).getElementsPerLong();
        long maxValue = ((IPackedIntegerArray) array).getMaxValue();
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

}
