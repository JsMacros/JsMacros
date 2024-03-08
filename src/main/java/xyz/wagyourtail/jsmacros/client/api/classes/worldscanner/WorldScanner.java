package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.access.IPackedIntegerArray;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainerData;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    @Nullable
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
    public WorldScanner(World world, @Nullable Function<BlockHelper, Boolean> blockFilter, @Nullable Function<BlockStateHelper, Boolean> stateFilter) {
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
    public List<Pos3D> scanAroundPlayer(int chunkRange) {
        if (mc.player == null) return new ArrayList<>();
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
    public List<Pos3D> scanChunkRange(int centerX, int centerZ, int chunkrange) {
        assert world != null;
        if (chunkrange < 0) {
            throw new IllegalArgumentException("chunkrange must be at least 0");
        }
        return scanChunksInternal(getChunkRange(centerX, centerZ, chunkrange));
    }

    /**
     * scan area in blocks
     * @since 1.9.0
     */
    public List<Pos3D> scanCubeArea(BlockPosHelper pos, int range) {
        return scanCubeArea(pos.getX(), pos.getY(), pos.getZ(), range);
    }

    /**
     * scan area in blocks
     * @since 1.9.0
     */
    public List<Pos3D> scanCubeArea(int x, int y, int z, int range) {
        if (range < 0) throw new IllegalArgumentException("range cannot be negative!");
        return scanCubeAreaInternal(
                x - range, y - range, z - range,
                x + range, y + range, z + range
        ).collect(Collectors.toList());
    }

    /**
     * scan area in blocks
     * @param pos1 first pos, inclusive
     * @param pos2 second pos, exclusive
     * @since 1.9.0
     */
    public List<Pos3D> scanCubeArea(BlockPosHelper pos1, BlockPosHelper pos2) {
        return scanCubeArea(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    /**
     * scan area in blocks
     * @param x1 first x coordinate, inclusive
     * @param y1 first y coordinate, inclusive
     * @param z1 first z coordinate, inclusive
     * @param x2 second x coordinate, exclusive
     * @param y2 second y coordinate, exclusive
     * @param z2 second z coordinate, exclusive
     * @since 1.9.0
     */
    public List<Pos3D> scanCubeArea(int x1, int y1, int z1, int x2, int y2, int z2) {
        if (x1 == x2 || y1 == y2 || z1 == z2) return new ArrayList<>();
        return scanCubeAreaInternal(
                x1, y1, z1,
                x2 - ((x2 - x1) >> 31 | 1),
                y2 - ((y2 - y1) >> 31 | 1),
                z2 - ((z2 - z1) >> 31 | 1)
        ).collect(Collectors.toList());
    }

    /**
     * scan area in blocks
     * @param pos1 first pos, inclusive
     * @param pos2 second pos, inclusive
     * @since 1.9.0
     */
    public List<Pos3D> scanCubeAreaInclusive(BlockPosHelper pos1, BlockPosHelper pos2) {
        return scanCubeAreaInclusive(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    /**
     * scan area in blocks
     * @param x1 first x coordinate, inclusive
     * @param y1 first y coordinate, inclusive
     * @param z1 first z coordinate, inclusive
     * @param x2 second x coordinate, inclusive
     * @param y2 second y coordinate, inclusive
     * @param z2 second z coordinate, inclusive
     * @since 1.9.0
     */
    public List<Pos3D> scanCubeAreaInclusive(int x1, int y1, int z1, int x2, int y2, int z2) {
        return scanCubeAreaInternal(x1, y1, z1, x2, y2, z2).collect(Collectors.toList());
    }

    /**
     * scan area in blocks
     * @since 1.9.0
     */
    public List<Pos3D> scanSphereArea(Pos3D pos, double radius) {
        return scanSphereArea(pos.x, pos.y, pos.z, radius);
    }

    /**
     * scan area in blocks
     * @since 1.9.0
     */
    public List<Pos3D> scanSphereArea(double x, double y, double z, double radius) {
        if (radius < 0) throw new IllegalArgumentException("radius cannot be negative!");
        double sq = radius * radius;
        Vec3d centered = new Vec3d(x - 0.5, y - 0.5, z - 0.5);

        if (radius < 48) return scanCubeAreaInternal(
                (int) Math.floor(x - radius),
                (int) Math.floor(y - radius),
                (int) Math.floor(z - radius),
                (int) Math.floor(x + radius),
                (int) Math.floor(y + radius),
                (int) Math.floor(z + radius)
        ).filter(pos -> centered.squaredDistanceTo(pos.x, pos.y, pos.z) <= sq).collect(Collectors.toList());

        // skip edge chunk sections because of large radius
        Stream<ChunkPos> stream = ChunkPos.stream(
                new ChunkPos((int) Math.floor(x - radius) >> 4, (int) Math.floor(z - radius) >> 4),
                new ChunkPos((int) Math.floor(x + radius) >> 4, (int) Math.floor(z + radius) >> 4)
        );
        if (useParallelStream) //noinspection DataFlowIssue
            stream = stream.parallel();
        return stream.flatMap(chunkPos -> {
                    double dx = MathHelper.clamp(centered.x, chunkPos.getStartX(), chunkPos.getEndX()) - centered.x;
                    double dz = MathHelper.clamp(centered.z, chunkPos.getStartZ(), chunkPos.getEndZ()) - centered.z;
                    double xzDistSq = dx * dx + dz * dz;
                    if (xzDistSq > sq) return null;

                    double ry = Math.sqrt(sq - xzDistSq);
                    return scanChunkInternal(chunkPos, (int) Math.floor(centered.y - ry), (int) Math.floor(centered.y + ry));
                })
                .filter(pos -> centered.squaredDistanceTo(pos.x, pos.y, pos.z) <= sq)
                .collect(Collectors.toList());
    }

    /**
     * scan around with player pos and player reach.<br>
     * this doesn't filter out positions that has obstacle.
     * @since 1.9.0
     */
    public List<Pos3D> scanReachable() {
        if (mc.player == null) return new ArrayList<>();
        return scanReachable(new Pos3D(mc.player.getEyePos()), getReach(), true);
    }

    /**
     * scan around with player pos and player reach.<br>
     * this doesn't filter out positions that has obstacle.
     * @param strict if it should check for block outline instead of full cube, default is true
     * @since 1.9.0
     */
    public List<Pos3D> scanReachable(boolean strict) {
        if (mc.player == null) return new ArrayList<>();
        return scanReachable(new Pos3D(mc.player.getEyePos()), getReach(), strict);
    }

    /**
     * scan around with the given pos and player reach.<br>
     * this doesn't filter out positions that has obstacle.
     * @since 1.9.0
     */
    public List<Pos3D> scanReachable(Pos3D pos) {
        return scanReachable(pos, getReach(), true);
    }

    /**
     * scan around with the given pos and the given reach.<br>
     * this doesn't filter out positions that has obstacle.
     * @since 1.9.0
     */
    public List<Pos3D> scanReachable(Pos3D pos, double reach) {
        return scanReachable(pos, reach, true);
    }

    /**
     * scan around with the given pos and the given reach.<br>
     * this doesn't filter out positions that has obstacle.
     * @param pos {@code Player.getPlayer().getEyePos()}
     * @param reach {@code Player.getInteractionManager().getReach()}
     * @param strict if it should check for block outline instead of full cube, default is true
     * @since 1.9.0
     */
    public List<Pos3D> scanReachable(Pos3D pos, double reach, boolean strict) {
        return scanReachableInternal(pos.toMojangDoubleVector(), reach, strict).collect(Collectors.toList());
    }

    /**
     * scan around with player pos and player reach, and return the closest one.<br>
     * this doesn't filter out positions that has obstacle.
     * @since 1.9.0
     */
    @Nullable
    public Pos3D scanClosestReachable() {
        if (mc.player == null) return null;
        return scanClosestReachable(new Pos3D(mc.player.getEyePos()), getReach(), true);
    }

    /**
     * scan around with player pos and player reach, and return the closest one.<br>
     * this doesn't filter out positions that has obstacle.
     * @since 1.9.0
     */
    @Nullable
    public Pos3D scanClosestReachable(boolean strict) {
        if (mc.player == null) return null;
        return scanClosestReachable(new Pos3D(mc.player.getEyePos()), getReach(), strict);
    }

    /**
     * scan around with player pos and player reach, and return the closest one.<br>
     * this doesn't filter out positions that has obstacle.
     * @since 1.9.0
     */
    @Nullable
    public Pos3D scanClosestReachable(Pos3D pos, double reach, boolean strict) {
        Vec3d vec = pos.toMojangDoubleVector();
        Vec3d centered = vec.subtract(0.5, 0.5, 0.5);
        return scanReachableInternal(vec, reach, strict)
                .min(Comparator.comparingDouble(p -> centered.squaredDistanceTo(p.x, p.y, p.z)))
                .orElse(null);
    }

    private double getReach() {
        return mc.interactionManager != null ? mc.interactionManager.getReachDistance() : 4.5;
    }

    /**
     * all inclusive
     * @since 1.9.0
     */
    private Stream<Pos3D> scanCubeAreaInternal(int x1, int y1, int z1, int x2, int y2, int z2) {
        int worldBottom = world.getBottomY();
        int worldTop = world.getTopY() - 1;
        if (Math.min(y1, y2) > worldTop || Math.max(y1, y2) < worldBottom) return Stream.empty();

        y1 = MathHelper.clamp(y1, worldBottom, worldTop);
        y2 = MathHelper.clamp(y2, worldBottom, worldTop);
        int dx = (x2 - x1) >> 31 | 1;
        int dy = (y2 - y1) >> 31 | 1;
        int dz = (z2 - z1) >> 31 | 1;

        int size = Math.abs((x2 - x1 + dx) * (y2 - y1 + 1) * (z2 - z1 + 1));
        if (size < 255) { // honestly idk where's the threshold
            if (size == 0) return Stream.empty();
            List<Pos3D> list = new ArrayList<>();
            BlockPos.Mutable pos = new BlockPos.Mutable();
            for (int y = y1; (y < y2 ^ dy == -1) || y == y2; y += dy)
                for (int z = z1; (z < z2 ^ dz == -1) || z == z2; z += dz)
                    for (int x = x1; (x < x2 ^ dx == -1) || x == x2; x += dx) {
                        if (getFilterResult(world.getBlockState(pos.set(x, y, z)))) {
                            list.add(new Pos3D(x, y, z));
                        }
                    }
            return getBestStream(list);
        }

        int minX = dx == 1 ? x1 : x2;
        int minY = dy == 1 ? y1 : y2;
        int minZ = dz == 1 ? z1 : z2;
        int maxX = dx == 1 ? x2 : x1;
        int maxY = dy == 1 ? y2 : y1;
        int maxZ = dz == 1 ? z2 : z1;
        Stream<ChunkPos> stream = ChunkPos.stream(
                new ChunkPos(x1 >> 4, z1 >> 4),
                new ChunkPos(x2 >> 4, z2 >> 4)
        );
        if (useParallelStream) //noinspection DataFlowIssue
            stream = stream.parallel();
        return stream.flatMap(chunkPos -> scanChunkInternal(chunkPos, minY, maxY))
                .filter(pos ->
                        minX <= pos.x && pos.x <= maxX &&
                        minY <= pos.y && pos.y <= maxY &&
                        minZ <= pos.z && pos.z <= maxZ
                );
    }

    /**
     * @since 1.9.0
     */
    private Stream<Pos3D> scanReachableInternal(Vec3d pos, double reach, boolean strict) {
        if (reach < 0) throw new IllegalArgumentException("reach cannot be negative!");
        double sq = reach * reach;

        Stream<Pos3D> stream = scanCubeAreaInternal(
                (int) Math.floor(pos.x - reach),
                (int) Math.floor(pos.y - reach),
                (int) Math.floor(pos.z - reach),
                (int) Math.floor(pos.x + reach),
                (int) Math.floor(pos.y + reach),
                (int) Math.floor(pos.z + reach)
        ).filter(p -> pos.squaredDistanceTo(
                MathHelper.clamp(pos.x, p.x, p.x + 1),
                MathHelper.clamp(pos.y, p.y, p.y + 1),
                MathHelper.clamp(pos.z, p.z, p.z + 1)
        ) <= sq);

        return !strict ? stream : stream.filter(p -> {
            BlockPos blockPos = p.toRawBlockPos();
            VoxelShape vs = world.getBlockState(blockPos).getOutlineShape(world, blockPos);
            if (vs.isEmpty()) return false;
            if (VoxelShapes.fullCube().equals(vs)) return true;

            Vec3d relative = pos.subtract(p.x, p.y, p.z);
            boolean[] isInRange = {false};
            vs.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                if (isInRange[0]) return;
                isInRange[0] = relative.squaredDistanceTo(
                        MathHelper.clamp(relative.x, minX, maxX),
                        MathHelper.clamp(relative.y, minY, maxY),
                        MathHelper.clamp(relative.z, minZ, maxZ)
                ) <= sq;
            });
            return isInRange[0];
        });
    }

    private List<Pos3D> scanChunksInternal(List<ChunkPos> chunkPositions) {
        assert world != null;
        return getBestStream(chunkPositions).flatMap(this::scanChunkInternal).collect(Collectors.toList());
    }

    private Stream<Pos3D> scanChunkInternal(ChunkPos pos) {
        return scanChunkInternal(pos, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private Stream<Pos3D> scanChunkInternal(ChunkPos pos, int minY, int maxY) {
        if (!world.isChunkLoaded(pos.x, pos.z)) {
            return Stream.empty();
        }

        long chunkX = (long) pos.x << 4;
        long chunkZ = (long) pos.z << 4;

        List<Pos3D> blocks = new ArrayList<>();

        streamChunkSections(world.getChunk(pos.x, pos.z), minY, maxY, (section, yOffset, isInFilter) -> {
            PackedIntegerArray array = (PackedIntegerArray) ((IPalettedContainer<?>) section.getBlockStateContainer()).jsmacros_getData().jsmacros_getStorage();
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

    private Map<String, Integer> getBlocksInChunksInternal(List<ChunkPos> chunkPositions, boolean ignoreState) {
        Object2IntOpenHashMap<String> result = new Object2IntOpenHashMap<>();

        getBestStream(chunkPositions).flatMap(pos -> {
            if (!world.getChunkManager().isChunkLoaded(pos.x, pos.z)) {
                return Stream.empty();
            }

            Object2IntOpenHashMap<BlockState> blocks = new Object2IntOpenHashMap<>();

            streamChunkSections(world.getChunk(pos.x, pos.z), (section, yOffset, isInFilter) -> count(section.getBlockStateContainer(), isInFilter, blocks::addTo));
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

    private void streamChunkSections(Chunk chunk, TriConsumer<ChunkSection, Integer, boolean[]> consumer) {
        streamChunkSections(chunk, Integer.MIN_VALUE, Integer.MAX_VALUE, consumer);
    }

    private void streamChunkSections(Chunk chunk, int minY, int maxY, TriConsumer<ChunkSection, Integer, boolean[]> consumer) {
        int yOffset = chunk.getBottomY() - 16;
        minY &= ~15;
        for (ChunkSection section : chunk.getSectionArray()) {
            yOffset += 16;
            if (yOffset < minY) continue;
            if (yOffset > maxY) break;
            if (section == null || section.isEmpty()) {
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
            consumer.accept(section, yOffset, isInFilter);
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

    @FunctionalInterface
    private interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);

    }

}
