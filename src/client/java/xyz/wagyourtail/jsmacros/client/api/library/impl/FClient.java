package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.api.helper.ModContainerHelper;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.ServerInfoHelper;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.client.tick.TickSync;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * Functions that interact with minecraft that don't fit into their own module.
 * <p>
 * An instance of this class is passed to scripts as the {@code Client} variable.
 *
 * @author Wagyourtail
 * @since 1.2.9
 */
@Library("Client")
@SuppressWarnings("unused")
public class FClient extends PerExecLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Don't touch this plz xd.
     */
    public static TickSync tickSynchronizer = new TickSync();

    public FClient(BaseScriptContext<?> context) {
        super(context);
    }

    /**
     * @return the raw minecraft client class, it may be useful to use <a target="_blank" href="https://wagyourtail.xyz/Projects/Minecraft%20Mappings%20Viewer/App">Minecraft Mappings Viewer</a> for this.
     * @since 1.0.0 (was in the {@code jsmacros} library until 1.2.9)
     */
    public MinecraftClient getMinecraft() {
        return mc;
    }

    /**
     * @return a helper for interacting with minecraft's registry.
     * @since 1.8.4
     */
    public RegistryHelper getRegistryManager() {
        return new RegistryHelper();
    }

    /**
     * @return a helper to modify and send minecraft packets.
     * @since 1.8.4
     */
    public PacketByteBufferHelper createPacketByteBuffer() {
        return new PacketByteBufferHelper();
    }

    /**
     * Run your task on the main minecraft thread
     *
     * @param runnable task to run
     * @since 1.4.0
     */
    public void runOnMainThread(MethodWrapper<Object, Object, Object, ?> runnable) throws InterruptedException {
        runOnMainThread(runnable, false, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
    }

    /**
     *
     * @param runnable
     * @param watchdogMaxTime
     * @throws InterruptedException
     * @since 1.6.5
     */
    public void runOnMainThread(MethodWrapper<Object, Object, Object, ?> runnable, long watchdogMaxTime) throws InterruptedException {
        runOnMainThread(runnable, false, watchdogMaxTime);
    }

    /**
     * @param runnable
     * @param await
     * @param watchdogMaxTime max time for the watchdog to wait before killing the script
     * @since 1.9.1
     */
    public void runOnMainThread(MethodWrapper<Object, Object, Object, ?> runnable, boolean await, long watchdogMaxTime) throws InterruptedException {
        if (mc.isOnThread()) {
            runnable.run();
        } else if (runner.profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on main thread while currently joined to main!");
        } else {
            Semaphore semaphore = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                EventContainer<?> lock = new EventContainer<>(runnable.getCtx());
                lock.setLockThread(Thread.currentThread());
                EventLockWatchdog.startWatchdog(lock, new IEventListener() {
                    @Override
                    public boolean joined() {
                        return false;
                    }

                    @Override
                    public EventContainer<?> trigger(BaseEvent event) {
                        return null;
                    }

                    @Override
                    public String toString() {
                        return "RunOnMainThread{\"called_by\": " + runnable.getCtx().getTriggeringEvent().toString() + "}";
                    }
                }, watchdogMaxTime);
                boolean success = false;
                try {
                    runnable.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    lock.releaseLock();
                    semaphore.release();
                }
            });

            if (await) {
                ctx.wrapSleep(semaphore::acquire);
            }
        }
    }

    /**
     * @return a helper which gives access to all game options and some other useful features.
     * @since 1.1.7 (was in the {@code jsmacros} library until 1.2.9)
     */
    public OptionsHelper getGameOptions() {
        return new OptionsHelper(mc.options);
    }

    /**
     * @return the current minecraft version as a {@link String String}.
     * @since 1.1.2 (was in the {@code jsmacros} library until 1.2.9)
     */
    public String mcVersion() {
        return mc.getGameVersion();
    }

    /**
     * @return the fps debug string from minecraft.
     * @since 1.2.0 (was in the {@code jsmacros} library until 1.2.9)
     */
    public String getFPS() {
        return mc.fpsDebugString;
    }

    /**
     * Join singleplayer world
     *
     * @param folderName
     * @since 1.6.6
     */
    public void loadWorld(String folderName) throws LevelStorageException {

        LevelStorage levelstoragesource = mc.getLevelStorage();
        List<LevelStorage.LevelSave> levels = levelstoragesource.getLevelList().levels();
        if (levels.stream().noneMatch(e -> e.getRootPath().equals(folderName))) {
            throw new RuntimeException("Level Not Found!");
        }

        mc.execute(() -> {
            boolean bl = mc.isInSingleplayer();
            if (mc.world != null) {
                mc.world.disconnect(Text.of(""));
            }
            if (bl) {
                mc.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")), false);
            } else {
                mc.disconnect(null, false);
            }
            mc.createIntegratedServerLoader().start(folderName, () -> mc.setScreen(new TitleScreen()));
        });
    }

    /**
     * @param ip
     * @see #connect(String, int)
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     */
    public void connect(String ip) {
        ServerAddress a = ServerAddress.parse(ip);
        connect(a.getAddress(), a.getPort());
    }

    /**
     * Connect to a server
     *
     * @param ip
     * @param port
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     */
    public void connect(String ip, int port) {
        mc.execute(() -> {
            boolean bl = mc.isInSingleplayer();
            if (mc.world != null) {
                mc.world.disconnect(Text.of(""));
            }
            if (bl) {
                mc.disconnect(new MessageScreen(Text.of("Saving World")),false);
            } else {
                mc.disconnect(new MessageScreen(Text.of("")),false);
            }
            ConnectScreen.connect(null, mc, new ServerAddress(ip, port), new ServerInfo("server", new ServerAddress(ip, port).toString(), ServerInfo.ServerType.OTHER), false, null);
        });
    }

    /**
     * @see #disconnect(MethodWrapper)
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     */
    public void disconnect() {
        disconnect(null);
    }

    /**
     * Disconnect from a server with callback.
     *
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link Boolean Boolean}&gt;
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     * <p>
     * {@code callback} defaults to {@code null}
     */
    public void disconnect(@Nullable MethodWrapper<Boolean, Object, Object, ?> callback) {
        mc.execute(() -> {
            boolean isWorld = mc.world != null;
            boolean isInSingleplayer = mc.isInSingleplayer();
            if (isWorld) {
                // logic in death screen disconnect button
                if (mc.world != null) {
                    mc.world.disconnect(Text.of(""));
                }
                mc.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")), false);
                mc.setScreen(new TitleScreen());
            }
            if (isInSingleplayer) {
                mc.setScreen(new TitleScreen());
            } else if (mc.getCurrentServerEntry() != null) {
                if (mc.getCurrentServerEntry().isRealm()) {
                    mc.setScreen(new RealmsMainScreen(new TitleScreen()));
                } else {
                    mc.setScreen(new MultiplayerScreen(new TitleScreen()));
                }
            }
            try {
                if (callback != null) {
                    callback.accept(isWorld);
                }
            } catch (Throwable e) {
                runner.profile.logError(e);
            }
        });
    }

    /**
     * Closes the client (stops the game).
     * Waits until the game has stopped, meaning no further code is executed (for obvious reasons).
     * Warning: this does not wait on joined threads, so your script may stop at an undefined point.
     *
     * @since 1.6.0
     */
    @DocletReplaceReturn("never")
    public void shutdown() {
        mc.execute(mc::scheduleStop);

        if (!runner.profile.checkJoinedThreadStack()) {
            // Wait until the game stops
            while (true) {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    /**
     * @throws InterruptedException
     * @see #waitTick(int)
     * @since 1.2.4
     */
    public void waitTick() throws InterruptedException {
        if (runner.profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }
        ctx.wrapSleep(tickSynchronizer::waitTick);
    }

    /**
     * waits the specified number of client ticks.
     * don't use this on an event that the main thread waits on (joins)... that'll cause circular waiting.
     *
     * @param i
     * @throws InterruptedException
     * @since 1.2.6
     */
    public void waitTick(int i) throws InterruptedException {
        if (runner.profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }
        ctx.wrapSleep(() -> {
            tickSynchronizer.waitTicks(i);
        });
    }

    /**
     * @param ip
     * @return
     * @throws UnknownHostException
     * @throws InterruptedException
     * @since 1.6.5
     */
    public ServerInfoHelper ping(String ip) throws UnknownHostException, InterruptedException {
        ServerInfo info = new ServerInfo("", ip, ServerInfo.ServerType.OTHER);
        if (runner.profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("pinging from main thread is not supported!");
        }
        Semaphore semaphore = new Semaphore(0);
        TickBasedEvents.serverListPinger.add(info, () -> {}, semaphore::release);
        semaphore.acquire();
        return new ServerInfoHelper(info);
    }

    /**
     * @param ip
     * @param callback
     * @throws UnknownHostException
     * @since 1.6.5
     */
    @DocletReplaceParams("ip: string, callback: MethodWrapper<ServerInfoHelper | null, java.io.IOException | null>")
    public void pingAsync(String ip, MethodWrapper<ServerInfoHelper, IOException, Object, ?> callback) {
        CompletableFuture.runAsync(() -> {
            ServerInfo info = new ServerInfo("", ip, ServerInfo.ServerType.OTHER);
            try {
                TickBasedEvents.serverListPinger.add(info, () -> {}, () -> callback.accept(new ServerInfoHelper(info), null));
            } catch (IOException e) {
                callback.accept(null, e);
            }
        });
    }

    /**
     * @since 1.6.5
     */
    public void cancelAllPings() {
        TickBasedEvents.serverListPinger.cancel();
    }

    /**
     * @return a list of all loaded mods.
     * @since 1.8.4
     */
    public List<? extends ModContainerHelper<?>> getLoadedMods() {
        return JsMacros.getModLoader().getLoadedMods();
    }

    /**
     * @param modId the mod modId
     * @return {@code true} if the mod with the given modId is loaded, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isModLoaded(String modId) {
        return JsMacros.getModLoader().isModLoaded(modId);
    }

    /**
     * @param modId the mod modId
     * @return the mod container for the given modId or {@code null} if the mod is not loaded.
     * @since 1.8.4
     */
    @Nullable
    public ModContainerHelper<?> getMod(String modId) {
        return JsMacros.getModLoader().getMod(modId);
    }

    /**
     * Makes minecraft believe that the mouse is currently inside the window.
     * This will automatically set pause on lost focus to false.
     *
     * @since 1.8.4
     */
    public void grabMouse() {
        mc.options.pauseOnLostFocus = false;
        mc.onWindowFocusChanged(true);
        mc.mouse.lockCursor();
    }

    /**
     * @return {@code true} if the mod is loaded inside a development environment, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDevEnv() {
        return JsMacros.getModLoader().isDevEnv();
    }

    /**
     * @return the name of the mod loader.
     * @since 1.8.4
     */
    public String getModLoader() {
        return JsMacros.getModLoader().getName();
    }

    /**
     * @return a list of all loaded blocks as {@link BlockHelper BlockHelper} objects.
     * @since 1.8.4
     */
    public List<BlockHelper> getRegisteredBlocks() {
        return Registries.BLOCK.stream().map(BlockHelper::new).collect(Collectors.toList());
    }

    /**
     * @return a list of all loaded items as {@link ItemHelper ItemHelper} objects.
     * @since 1.8.4
     */
    public List<ItemHelper> getRegisteredItems() {
        return Registries.ITEM.stream().map(ItemHelper::new).collect(Collectors.toList());
    }

    /**
     * Tries to peacefully close the game.
     *
     * @since 1.8.4
     */
    public void exitGamePeacefully() {
        mc.scheduleStop();
    }

    /**
     * Will close the game forcefully.
     *
     * @since 1.8.4
     */
    @DocletReplaceReturn("never")
    public void exitGameForcefully() {
        System.exit(0);
    }

    /**
     * @param packet the packet to send
     * @see #createPacketByteBuffer()
     * @since 1.8.4
     */
    public void sendPacket(Packet<?> packet) {
        ClientPlayNetworkHandler network = mc.getNetworkHandler();
        if (network != null) {
            network.sendPacket(packet);
        }
    }

    /**
     * @param packet the packet to receive
     * @see #createPacketByteBuffer()
     * @since 1.8.4
     */
    public void receivePacket(Packet<ClientPlayPacketListener> packet) {
        packet.apply(mc.getNetworkHandler());
    }

    /**
     * moved from FUtils
     * @since 2.0.0
     */
    public String getClipboard() {
        return mc.keyboard.getClipboard();
    }

    /**
     * moved from FUtils
     * @since 2.0.0
     */
    public void setClipboard(String text) {
        mc.keyboard.setClipboard(text);
    }

}
