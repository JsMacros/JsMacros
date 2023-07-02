package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import xyz.wagyourtail.jsmacros.client.access.IBossBarHud;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScanner;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScannerBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.*;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * Functions for getting and using world data.
 * 
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
     * @since 1.3.0
     * @return
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
        for (AbstractClientPlayerEntity p : ImmutableList.copyOf(mc.world.getPlayers())) {
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
     * 
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
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b, t, bp);
    }

    public BlockDataHelper getBlock(PositionCommon.Pos3D pos) {
        return getBlock((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public BlockDataHelper getBlock(BlockPosHelper pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Usage: <br>
     * This will return all blocks that are facing south, don't require a tool to break, 
     * have a hardness of 10 or less and whose name contains either chest or barrel.
     * <pre>
     * World.getWorldScanner()
     *     .withBlockFilter("getHardness").is("<=", 10)
     *     .andStringBlockFilter().contains("chest", "barrel")
     *     .withStringStateFilter().contains("facing=south")
     *     .andStateFilter("isToolRequired").is(false)
     *     .build()
     * </pre>
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
     * @since 1.6.4
     * @param id
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int centerX, int centerZ, String id, int chunkrange) {
        return new WorldScanner(mc.world, block -> Registry.BLOCK.getId(block.getRaw()).toString().equals(id), null).scanChunkRange(centerX, centerZ, chunkrange);
    }

    /**
     * @since 1.6.4
     * @param id
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(String id, int chunkrange) {
        assert mc.player != null;
        int playerChunkX = mc.player.getBlockX() >> 4;
        int playerChunkZ = mc.player.getBlockZ() >> 4;
        return new WorldScanner(mc.world, block -> Registry.BLOCK.getId(block.getRaw()).toString().equals(id), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }


    /**
     * @since 1.6.4
     * @param ids
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(String[] ids, int chunkrange) {
        assert mc.player != null;
        int playerChunkX = mc.player.getBlockX() >> 4;
        int playerChunkZ = mc.player.getBlockZ() >> 4;
        Set<String> ids2 = new HashSet<>(Arrays.asList(ids));
        return new WorldScanner(mc.world, block -> ids2.contains(Registry.BLOCK.getId(block.getRaw()).toString()), null).scanChunkRange(playerChunkX, playerChunkZ, chunkrange);
    }

    /**
     * @since 1.6.4
     * @param centerX
     * @param centerZ
     * @param ids
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int centerX, int centerZ, String[] ids, int chunkrange) {
        Set<String> ids2 = new HashSet<>(Arrays.asList(ids));
        return new WorldScanner(mc.world, block -> ids2.contains(Registry.BLOCK.getId(block.getRaw()).toString()), null).scanChunkRange(centerX, centerZ, chunkrange);
    }



    /**
     * @since 1.6.4
     * @param blockFilter
     * @param stateFilter
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) throw new IllegalArgumentException("idFilter cannot be null");
        assert mc.player != null;
        int playerChunkX = mc.player.getBlockX() >> 4;
        int playerChunkZ = mc.player.getBlockZ() >> 4;
        return findBlocksMatching(playerChunkX, playerChunkZ, blockFilter, stateFilter, chunkrange);
    }

    /**
     * @since 1.6.4
     * @param chunkX
     * @param chunkZ
     * @param blockFilter
     * @param stateFilter
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int chunkX, int chunkZ, MethodWrapper<BlockHelper, Object, Boolean, ?> blockFilter, MethodWrapper<BlockStateHelper, Object, Boolean, ?> stateFilter, int chunkrange) {
        if (blockFilter == null) throw new IllegalArgumentException("block filter cannot be null");
        return new WorldScanner(mc.world, blockFilter, stateFilter).scanChunkRange(chunkX, chunkZ, chunkrange);
    }

    /**
     * @since 1.2.9
     * @return a helper for the scoreboards provided to the client.
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
        for (Entity e : ImmutableList.copyOf(mc.world.getEntities())) {
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
        BlockHitResult result = mc.world.raycast(new RaycastContext(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2), RaycastContext.ShapeType.COLLIDER, fluid ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
        if (result.getType() != BlockHitResult.Type.MISS) {
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
        TargetPredicate target = TargetPredicate.createNonAttackable();
        target.setPredicate((e) -> e.getBoundingBox().raycast(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2)).isPresent());
        List<LivingEntity> entities = (List) StreamSupport.stream(mc.world.getEntities().spliterator(), false).filter(e -> e instanceof LivingEntity).collect(Collectors.toList());
        LivingEntity e = mc.world.getClosestEntity(entities, target, null, x1, y1, z1);
        if (e != null) {
            return EntityHelper.create(e);
        }
        return null;
    }

    /**
     * @since 1.1.2
     * @return the current dimension.
     */
    public String getDimension() {
        assert mc.world != null;
        return mc.world.getRegistryKey().getValue().toString();
    }
    
    /**
     * @since 1.1.5
     * @return the current biome.
     */
    public String getBiome() {
        assert mc.world != null;
        return mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(mc.world.getBiome(mc.player.getBlockPos()).value()).toString();
    }
    
    /**
     * @since 1.1.5
     * @return the current world time.
     */
    public long getTime() {
        assert mc.world != null;
        return mc.world.getTime();
    }
    
    /**
     * This is supposed to be time of day, but it appears to be the same as {@link FWorld#getTime()} to me...
     * @since 1.1.5
     * 
     * @return the current world time of day.
     */
    public long getTimeOfDay() {
        assert mc.world != null;
        return mc.world.getTimeOfDay();
    }
    
    /**
     * @since 1.2.6
     * @return respawn position.
     */
    public BlockPosHelper getRespawnPos() {
        assert mc.world != null;
        return new BlockPosHelper(mc.world.getSpawnPos());
    }
    
    /**
     * @since 1.2.6
     * @return world difficulty as an {@link java.lang.Integer Integer}.
     */
    public int getDifficulty() {
        assert mc.world != null;
        return mc.world.getDifficulty().getId();
    }
    
    /**
     * @since 1.2.6
     * @return moon phase as an {@link java.lang.Integer Integer}.
     */    
    public int getMoonPhase() {
        assert mc.world != null;
        return mc.world.getMoonPhase();
    }
    
    /**
     * @since 1.1.2
     * @param x
     * @param y
     * @param z
     * @return sky light as an {@link java.lang.Integer Integer}.
     */
    public int getSkyLight(int x, int y, int z) {
        assert mc.world != null;
        return mc.world.getLightLevel(LightType.SKY, new BlockPos(x, y, z));
    }
    
    /**
     * @since 1.1.2
     * @param x
     * @param y
     * @param z
     * @return block light as an {@link java.lang.Integer Integer}.
     */
    public int getBlockLight(int x, int y, int z) {
        assert mc.world != null;
        return mc.world.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
    }
    
    /**
     * plays a sound file using javax's sound stuff.
     * @since 1.1.7
     * 
     * @param file
     * @param volume
     * @return
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public Clip playSoundFile(String file, double volume) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(Core.getInstance().config.macroFolder, file)));
        FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        double min = gainControl.getMinimum();
        double range = gainControl.getMaximum() - min;
        float gain = (float) ((range * volume) + min);
        gainControl.setValue(gain);
        clip.addLineListener(event -> {
            if(event.getType().equals(LineEvent.Type.STOP)) {
                clip.close();
            }
        });
        clip.start();
        return clip;
    }
    
    /**
     * @since 1.1.7
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @param id
     */
    public void playSound(String id) {
        playSound(id, 1F);
    }
    
    /**
     * @since 1.1.7
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @param id
     * @param volume
     */
    public void playSound(String id, double volume) {
        playSound(id, volume, 0.25F);
    }
    
    /**
     * @since 1.1.7
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @param id
     * @param volume
     * @param pitch
     */
    public void playSound(String id, double volume, double pitch) {
        SoundEvent sound = Registry.SOUND_EVENT.get(new Identifier(id));
        assert sound != null;
        mc.execute(() -> mc.getSoundManager().play(PositionedSoundInstance.master(sound, (float) pitch, (float) volume)));
    }
    
    /**
     * plays a minecraft sound using the internal system.
     * @since 1.1.7
     * @param id
     * @param volume
     * @param pitch
     * @param x
     * @param y
     * @param z
     */
    public void playSound(String id, double volume, double pitch, double x, double y, double z) {
        assert mc.world != null;
        SoundEvent sound = Registry.SOUND_EVENT.get(new Identifier(id));
        assert sound != null;
        mc.execute(() -> mc.world.playSound(x, y, z, sound, SoundCategory.MASTER, (float) volume, (float) pitch, true));
    }
    
    /**
     * @since 1.2.1
     * @return a map of boss bars by the boss bar's UUID.
     */
    public Map<String, BossBarHelper> getBossBars() {
        assert mc.inGameHud != null;
        Map<UUID, ClientBossBar> bars = ImmutableMap.copyOf(((IBossBarHud) mc.inGameHud.getBossBarHud()).jsmacros_GetBossBars());
        Map<String, BossBarHelper> out = new HashMap<>();
        for (Map.Entry<UUID, ClientBossBar> e : ImmutableList.copyOf(bars.entrySet())) {
            out.put(e.getKey().toString(), new BossBarHelper(e.getValue()));
        }
        return out;
    }
    
    /**
     * Check whether a chunk is within the render distance and loaded.
     * @since 1.2.2
     * @param chunkX
     * @param chunkZ
     * @return
     */
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        if (mc.world == null) return false;
        return mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }
    
    /**
     * @since 1.2.2
     * @return the current server address as a string ({@code server.address/server.ip:port}).
     */
    public String getCurrentServerAddress() {
        ClientPlayNetworkHandler h = mc.getNetworkHandler();
        if (h == null) return null;
        ClientConnection c = h.getConnection();
        if (c == null) return null;
        return c.getAddress().toString();
    }
    
    /**
     * @since 1.2.2 [Citation Needed]
     * @param x
     * @param z
     * @return biome at specified location, only works if the block/chunk is loaded.
     */
    public String getBiomeAt(int x, int z) {
        assert mc.world != null;
        return mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(mc.world.getBiome(new BlockPos(x, 10, z)).value()).toString();
    }
    
    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps with various timings.
     */
    public String getServerTPS() {
        return String.format("%.2f, 1M: %.1f, 5M: %.1f, 15M: %.1f", serverInstantTPS, server1MAverageTPS, server5MAverageTPS, server15MAverageTPS);
    }
    
    /**
     * @since 1.3.1
     * @return text helper for the top part of the tab list (above the players)
     */
    public TextHelper getTabListHeader() {
        Text header = ((IPlayerListHud)mc.inGameHud.getPlayerListHud()).jsmacros_getHeader();
        if (header != null) return new TextHelper(header);
        return null;
    }
    
    /**
     * @since 1.3.1
     * @return  text helper for the bottom part of the tab list (below the players)
     */
    public TextHelper getTabListFooter() {
        Text footer = ((IPlayerListHud)mc.inGameHud.getPlayerListHud()).jsmacros_getFooter();
        if (footer != null) return new TextHelper(footer);
        return null;
    }
    
    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps.
     */
    public double getServerInstantTPS() {
        return serverInstantTPS;
    }
    

    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps over the previous 1 minute average.
     */
    public double getServer1MAverageTPS() {
        return server1MAverageTPS;
    }
    

    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps over the previous 5 minute average.
     */
    public double getServer5MAverageTPS() {
        return server5MAverageTPS;
    }
    

    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps over the previous 15 minute average.
     */
    public double getServer15MAverageTPS() {
        return server15MAverageTPS;
    }
}
