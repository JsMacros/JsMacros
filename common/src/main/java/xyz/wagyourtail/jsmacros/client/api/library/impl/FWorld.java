package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2840;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.sound.Sound;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScanner;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScannerBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.*;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinBossBarHud;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Functions for getting and using world data.
 * An instance of this class is passed to scripts as the {@code World} variable.
 *
 * @author Wagyourtail
 */
@Library("World")
@SuppressWarnings("unused")
public class FWorld extends BaseLibrary {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Don't modify.
     */
    public static double serverInstantTPS = 20;
    /**
     * Don't modify.
     */
    public static double server1MAverageTPS = 20;
    /**
     * Don't modify.
     */
    public static double server5MAverageTPS = 20;
    /**
     * Don't modify.
     */
    public static double server15MAverageTPS = 20;

    /**
     * returns whether a world is currently loaded
     *
     * @return
     *
     * @since 1.3.0
     */
    public boolean isWorldLoaded() {
        return mc.world != null;
    }

    /**
     * @return players within render distance.
     */
    public List<PlayerEntityHelper<PlayerEntity>> getLoadedPlayers() {
        assert mc.world != null;
        List<PlayerEntityHelper<PlayerEntity>> players = new ArrayList<>();
        for (PlayerEntity p : ImmutableList.copyOf(mc.world.playerEntities)) {
            players.add(new PlayerEntityHelper<>(p));
        }
        return players;
    }

    /**
     * @return players on the tablist.
     */
    public List<PlayerListEntryHelper> getPlayers() {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        assert handler != null;
        List<PlayerListEntryHelper> players = new ArrayList<>();
        for (PlayerListEntry p : ImmutableList.copyOf(handler.getPlayerList())) {
            players.add(new PlayerListEntryHelper(p));
        }
        return players;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return The block at that position.
     */
    public BlockDataHelper getBlock(int x, int y, int z) {
        assert mc.world != null;
        BlockPos bp = new BlockPos(x, y, z);
        BlockState b = mc.world.getBlockState(bp);
        BlockEntity t = mc.world.getBlockEntity(bp);
        if (b.getBlock().equals(Blocks.AIR)) {
            return null;
        }
        return new BlockDataHelper(b, t, bp);
    }

    public BlockDataHelper getBlock(PositionCommon.Pos3D pos) {
        return getBlock((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public BlockDataHelper getBlock(BlockPosHelper pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Usage: <br> This will return all blocks that are facing south, don't require a tool to break,
     * have a hardness of 10 or less and whose name contains either chest or barrel.
     * <pre>
     * World.getWorldScanner()
     *     .withBlockFilter("getHardness").is("<=", 10)
     *     .andStringBlockFilter().contains("chest", "barrel")
     *     .withStringStateFilter().contains("facing=south")
     *     .andStateFilter("isToolRequired").is(false)
     *     .build()
     * </pre>
     *
     * @return a builder to create a WorldScanner
     *
     * @since 1.6.5
     */
    public WorldScannerBuilder getWorldScanner() {
        return new WorldScannerBuilder();
    }

    /**
     * @return a scanner for the current world
     *
     * @since 1.6.5
     */
    public WorldScanner getWorldScanner(MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter) {
        return new WorldScanner(mc.world, blockFilter, stateFilter);
    }

    /**
     * @param id
     * @param chunkrange
     * @return
     *
     * @since 1.6.4
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int centerX, int centerZ, String id, int chunkrange) {
        return new WorldScanner(mc.world, block -> Block.REGISTRY.getIdentifier(block.getRaw()).toString().equals(id), null).scanChunkRange(centerX, centerZ, chunkrange);
    }

    /**
     * @param id
     * @param chunkrange
     * @return
     *
     * @since 1.6.4
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(String id, int chunkrange) {
        assert mc.player != null;
        int playerChunkX = mc.player.getBlockPos().getX() >> 4;
        int playerChunkZ = mc.player.getBlockPos().getZ() >> 4;
        return new WorldScanner(mc.world, block -> Block.REGISTRY.getIdentifier(block.getRaw()).toString().equals(id), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    /**
     * @param ids
     * @param chunkrange
     * @return
     *
     * @since 1.6.4
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(String[] ids, int chunkrange) {
        assert mc.player != null;
        int playerChunkX = (int) mc.player.x >> 4;
        int playerChunkZ = (int) mc.player.z >> 4;
        Set<String> ids2 = new HashSet<>(Arrays.asList(ids));
        return new WorldScanner(mc.world, block -> ids2.contains(Block.REGISTRY.getIdentifier(block.getRaw()).toString()), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    /**
     * @param centerX
     * @param centerZ
     * @param ids
     * @param chunkrange
     * @return
     *
     * @since 1.6.4
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int centerX, int centerZ, String[] ids, int chunkrange) {
        Set<String> ids2 = new HashSet<>(Arrays.asList(ids));
        return new WorldScanner(mc.world, block -> ids2.contains(Block.REGISTRY.getIdentifier(block.getRaw()).toString()), null).scanChunkRange(centerX, centerZ, chunkrange);
    }

    /**
     * @param blockFilter
     * @param stateFilter
     * @param chunkrange
     * @return
     *
     * @since 1.6.4
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) {
            throw new IllegalArgumentException("idFilter cannot be null");
        }
        assert mc.player != null;
        int playerChunkX = mc.player.getBlockPos().getX() >> 4;
        int playerChunkZ = mc.player.getBlockPos().getZ() >> 4;
        return findBlocksMatching(playerChunkX, playerChunkZ, blockFilter, stateFilter, chunkrange);
    }

    /**
     * @param chunkX
     * @param chunkZ
     * @param blockFilter
     * @param stateFilter
     * @param chunkrange
     * @return
     *
     * @since 1.6.4
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int chunkX, int chunkZ, MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) {
            throw new IllegalArgumentException("block filter cannot be null");
        }
        return new WorldScanner(mc.world, blockFilter, stateFilter).scanChunkRange(chunkX, chunkZ, chunkrange);
    }

    /**
     * @return a helper for the scoreboards provided to the client.
     *
     * @since 1.2.9
     */
    public ScoreboardsHelper getScoreboards() {
        assert mc.world != null;
        return new ScoreboardsHelper(mc.world.getScoreboard());
    }

    /**
     * @return all entities in the render distance.
     */
    public List<EntityHelper<?>> getEntities() {
        assert mc.world != null;
        List<EntityHelper<?>> entities = new ArrayList<>();
        for (Entity e : ImmutableList.copyOf(mc.world.loadedEntities)) {
            entities.add(EntityHelper.create(e));
        }
        return entities;
    }

    /**
     * raytrace between two points returning the first block hit.
     *
     * @since 1.6.5
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param fluid
     *
     * @return
     */
    public BlockDataHelper rayTraceBlock(int x1, int y1, int z1, int x2, int y2, int z2, boolean fluid) {
        HitResult result = mc.world.rayTrace(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2), fluid);
        if (result != null) {
            return new BlockDataHelper(mc.world.getBlockState(result.getBlockPos()), mc.world.getBlockEntity(result.getBlockPos()), result.getBlockPos());
        }
        return null;
    }

    /**
     * raytrace between two points returning the first entity hit.
     * @since 1.8.3
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public EntityHelper<?> rayTraceEntity(int x1, int y1, int z1, int x2, int y2, int z2) {
        return null;
    }

    /**
     * @since 1.1.2
     * @return the current dimension.
     *
     * @since 1.1.2
     */
    public String getDimension() {
        assert mc.world != null;
        return mc.world.getLevelProperties().getLevelName();
    }

    /**
     * @return the current biome.
     *
     * @since 1.1.5
     */
    public String getBiome() {
        assert mc.world != null;
        assert mc.player != null;
        return mc.world.getBiome(mc.player.getBlockPos()).getName();
    }

    /**
     * @return the current world time.
     *
     * @since 1.1.5
     */
    public long getTime() {
        assert mc.world != null;
        return mc.world.getLastUpdateTime();
    }

    /**
     * This is supposed to be time of day, but it appears to be the same as {@link FWorld#getTime()}
     * to me...
     *
     * @return the current world time of day.
     *
     * @since 1.1.5
     */
    public long getTimeOfDay() {
        assert mc.world != null;
        return mc.world.getTimeOfDay();
    }

    /**
     * @return respawn position.
     *
     * @since 1.2.6
     */
    public BlockPosHelper getRespawnPos() {
        assert mc.world != null;
        return new BlockPosHelper(mc.world.getSpawnPos());
    }

    /**
     * @return world difficulty as an {@link java.lang.Integer Integer}.
     *
     * @since 1.2.6
     */
    public int getDifficulty() {
        assert mc.world != null;
        return mc.world.getGlobalDifficulty().getId();
    }

    /**
     * @return moon phase as an {@link java.lang.Integer Integer}.
     *
     * @since 1.2.6
     */
    public int getMoonPhase() {
        assert mc.world != null;
        return (int) (mc.world.getMoonPhase() * 4);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return sky light as an {@link java.lang.Integer Integer}.
     *
     * @since 1.1.2
     */
    public int getSkyLight(int x, int y, int z) {
        assert mc.world != null;
        return mc.world.getLightAtPos(LightType.SKY, new BlockPos(x, y, z));
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return block light as an {@link java.lang.Integer Integer}.
     *
     * @since 1.1.2
     */
    public int getBlockLight(int x, int y, int z) {
        assert mc.world != null;
        return mc.world.getLightAtPos(LightType.BLOCK, new BlockPos(x, y, z));
    }

    /**
     * plays a sound file using javax's sound stuff.
     *
     * @param file
     * @param volume
     * @return
     *
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @since 1.1.7
     */
    public Clip playSoundFile(String file, double volume) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(Core.getInstance().config.macroFolder, file)));
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        double min = gainControl.getMinimum();
        double range = gainControl.getMaximum() - min;
        float gain = (float) ((range * volume) + min);
        gainControl.setValue(gain);
        clip.addLineListener(event -> {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                clip.close();
            }
        });
        clip.start();
        return clip;
    }

    /**
     * @param id
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @since 1.1.7
     */
    public void playSound(String id) {
        playSound(id, 1F);
    }

    /**
     * @param id
     * @param volume
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @since 1.1.7
     */
    public void playSound(String id, double volume) {
        playSound(id, volume, 0.25F);
    }

    /**
     * @param id
     * @param volume
     * @param pitch
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @since 1.1.7
     */
    public void playSound(String id, double volume, double pitch) {
        Identifier sound = new Identifier(id);
        assert sound != null;
        assert mc.player != null;
        double x = mc.player.x;
        double y = mc.player.y;
        double z = mc.player.z;
        mc.execute(() -> mc.getSoundManager().play(new PositionedSoundInstance(Objects.requireNonNull(Sound.REGISTRY.get(sound)), SoundCategory.MASTER, (float) volume, (float) pitch, new BlockPos(x, y, z))));
    }

    /**
     * plays a minecraft sound using the internal system.
     *
     * @param id
     * @param volume
     * @param pitch
     * @param x
     * @param y
     * @param z
     * @since 1.1.7
     */
    public void playSound(String id, double volume, double pitch, double x, double y, double z) {
        Identifier sound = new Identifier(id);
        assert sound != null;
        mc.execute(() -> mc.getSoundManager().play(new PositionedSoundInstance(Objects.requireNonNull(Sound.REGISTRY.get(sound)), SoundCategory.MASTER, (float) volume, (float) pitch, new BlockPos(x, y, z))));
    }

    /**
     * @return a map of boss bars by the boss bar's UUID.
     *
     * @since 1.2.1
     */
    public Map<String, BossBarHelper> getBossBars() {
        return  ((MixinBossBarHud) mc.inGameHud.method_12167()).getBossBars().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> new BossBarHelper(e.getValue())));
    }

    /**
     * Check whether a chunk is within the render distance and loaded.
     *
     * @param chunkX
     * @param chunkZ
     * @return
     *
     * @since 1.2.2
     */
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        if (mc.world == null) {
            return false;
        }
        return mc.world.getChunkProvider().isChunkGenerated(chunkX, chunkZ);
    }

    /**
     * @return the current server address as a string ({@code server.address/server.ip:port}).
     *
     * @since 1.2.2
     */
    public String getCurrentServerAddress() {
        ClientConnection h = mc.getNetworkHandler().getClientConnection();
        if (h == null) {
            return null;
        }
        SocketAddress c = h.getAddress();
        if (c == null) {
            return null;
        }
        return c.toString();
    }

    /**
     * @param x
     * @param z
     * @return biome at specified location, only works if the block/chunk is loaded.
     *
     * @since 1.2.2 [Citation Needed]
     */
    public String getBiomeAt(int x, int z) {
        assert mc.world != null;
        return mc.world.getBiome(new BlockPos(x, 10, z)).getName();
    }

    /**
     * @return best attempt to measure and give the server tps with various timings.
     *
     * @since 1.2.7
     */
    public String getServerTPS() {
        return String.format("%.2f, 1M: %.1f, 5M: %.1f, 15M: %.1f", serverInstantTPS, server1MAverageTPS, server5MAverageTPS, server15MAverageTPS);
    }

    /**
     * @return text helper for the top part of the tab list (above the players)
     *
     * @since 1.3.1
     */
    public TextHelper getTabListHeader() {
        Text header = ((IPlayerListHud) mc.inGameHud.getPlayerListWidget()).jsmacros_getHeader();
        if (header != null) {
            return new TextHelper(header);
        }
        return null;
    }

    /**
     * @return text helper for the bottom part of the tab list (below the players)
     *
     * @since 1.3.1
     */
    public TextHelper getTabListFooter() {
        Text footer = ((IPlayerListHud) mc.inGameHud.getPlayerListWidget()).jsmacros_getFooter();
        if (footer != null) {
            return new TextHelper(footer);
        }
        return null;
    }

    /**
     * @return best attempt to measure and give the server tps.
     *
     * @since 1.2.7
     */
    public double getServerInstantTPS() {
        return serverInstantTPS;
    }

    /**
     * @return best attempt to measure and give the server tps over the previous 1 minute average.
     *
     * @since 1.2.7
     */
    public double getServer1MAverageTPS() {
        return server1MAverageTPS;
    }

    /**
     * @return best attempt to measure and give the server tps over the previous 5 minute average.
     *
     * @since 1.2.7
     */
    public double getServer5MAverageTPS() {
        return server5MAverageTPS;
    }

    /**
     * @return best attempt to measure and give the server tps over the previous 15 minute average.
     *
     * @since 1.2.7
     */
    public double getServer15MAverageTPS() {
        return server15MAverageTPS;
    }
}
