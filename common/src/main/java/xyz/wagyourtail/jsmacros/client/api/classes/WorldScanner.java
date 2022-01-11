package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldScanner {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final World world;

    public WorldScanner(ClientWorld world) {
        this.world = world;
    }

    public PositionCommon.Pos2D getPlayerChunk() {
        ChunkPos playerChunk = mc.player.getChunkPos();
        return new PositionCommon.Pos2D(playerChunk.x, playerChunk.z);
    }

    public List<PositionCommon.Pos3D> scanAroundPlayer(int range, Function<Block, Boolean> blockFilter, Function<BlockState, Boolean> stateFilter) {
        assert mc.player != null;
        return findBlocksMatchingInternal(mc.player.getChunkPos().x, mc.player.getChunkPos().z, range, blockFilter, stateFilter);
    }

    // TODO: Different filtering methods
    private List<PositionCommon.Pos3D> findBlocksMatchingInternal(int centerX, int centerZ, int chunkrange, Function<Block, Boolean> blockFilter, Function<BlockState, Boolean> stateFilter) {
        assert mc.world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }

        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = centerX - chunkrange; x <= centerX + chunkrange; x++) {
            for (int z = centerZ - chunkrange; z <= centerZ + chunkrange; z++) {
                if (mc.world.isChunkLoaded(x, z)) {
                    chunks.add(new ChunkPos(x, z));
                }
            }
        }

        return findBlocksMatchingInternal(chunks, blockFilter, stateFilter);
    }

    private List<PositionCommon.Pos3D> findBlocksMatchingInternal(List<ChunkPos> pos, Function<Block, Boolean> blockFilter, Function<BlockState, Boolean> stateFilter) {
        assert mc.world != null;

        return pos.stream().parallel().flatMap(c -> {
            if (!mc.world.isChunkLoaded(c.x, c.z)) {
                return Stream.empty();
            }
            Chunk chunk = mc.world.getChunk(c.x, c.z);

            long chunkX = (long) c.x << 4;
            long chunkZ = (long) c.z << 4;

            List<PositionCommon.Pos3D> blocks = new ArrayList<>();

            for (ChunkSection section : chunk.getSectionArray()) {
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
                    if ((blockFilter != null && blockFilter.apply(state.getBlock())) || (stateFilter != null && stateFilter.apply(state))) {
                        commonBlockFound = true;
                        isInFilter[i] = true;
                    } else {
                        isInFilter[i] = false;
                    }
                }

                if (!commonBlockFound) {
                    continue;
                }

                int yOffset = section.getYOffset();

                forEach(array, (id, place) -> {
                    long x = chunkX + ((place & 255) & 15);
                    long y = yOffset + (place >> 8);
                    long z = chunkZ + ((place & 255) >> 4);

                    if (isInFilter[id]) {
                        blocks.add(new PositionCommon.Pos3D(x, y, z));
                    }
                });
            }
            return blocks.stream();
        }).collect(Collectors.toList());
    }

    private void forEach(PackedIntegerArray array, IntBiConsumer action) {
        int counter = 0;
        long[] data = array.getData();

        int elementsPerLong = ((IPackedIntegerArray) array).getElementsPerLong();
        long maxValue = ((IPackedIntegerArray) array).getMaxValue();
        int elementBits = array.getElementBits();
        int size = array.getSize();

        for (long datum : data) {
            long row = datum;
            if (row == 0) {
                counter += elementsPerLong;
                continue;
            }
            for (int idx = 0; idx < elementsPerLong; idx++) {
                action.accept((int) (row & maxValue), counter);
                row >>= elementBits;
                counter++;
                if (counter >= size) {
                    return;
                }
            }
        }
    }

    @FunctionalInterface
    private interface IntBiConsumer {
        void accept(int stateId, int position);
    }

}
