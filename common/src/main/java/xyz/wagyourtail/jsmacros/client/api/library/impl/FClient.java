package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ServerAddress;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ServerInfoHelper;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.client.tick.TickSync;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

/**
*
* Functions that interact with minecraft that don't fit into their own module.
*
 * An instance of this class is passed to scripts as the {@code Client} variable.
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
    *
    * @since 1.0.0 (was in the {@code jsmacros} library until 1.2.9)
     * @return the raw minecraft client class, it may be useful to use <a target="_blank" href="https://wagyourtail.xyz/Projects/Minecraft%20Mappings%20Viewer/App">Minecraft Mappings Viewer</a> for this.
     */
    public MinecraftClient getMinecraft() {
        return mc;
    }

    /**
     * Run your task on the main minecraft thread
     * @param runnable task to run
     * @since 1.4.0
     */
    public void runOnMainThread(MethodWrapper<Object, Object, Object, ?> runnable) {
        runOnMainThread(runnable, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
    }

    /**
     * @since 1.6.5
     * @param runnable
     * @param watchdogMaxTime max time for the watchdog to wait before killing the script
     */
    public void runOnMainThread(MethodWrapper<Object, Object, Object, ?> runnable, long watchdogMaxTime) {
        mc.execute(() -> {
            EventContainer<?> lock = new EventContainer<>(runnable.getCtx());
            lock.setLockThread(Thread.currentThread());
            EventLockWatchdog.startWatchdog(lock, new IEventListener() {
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
            }
            lock.releaseLock();
        });
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper
     *
     * @since 1.1.7 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @return an {@link xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper OptionsHelper} for the game options.
     */
    public OptionsHelper getGameOptions() {
        return new OptionsHelper(mc.options);
    }
    
    /**
     * @return the current minecraft version as a {@link java.lang.String String}.
     *
     * @since 1.1.2 (was in the {@code jsmacros} library until 1.2.9)
     */
    public String mcVersion() {
        return mc.getGameVersion();
    }
    
    /**
     * @since 1.2.0 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @return the fps debug string from minecraft.
     *
     */
    public String getFPS() {
        return mc.fpsDebugString;
    }
    
    /**
     * Join singleplayer world
     *
     * @since 1.6.6
     *
     * @param folderName
     */
    public void loadWorld(String folderName) throws LevelStorageException {

        LevelStorage levelstoragesource = mc.getLevelStorage();
        List<LevelSummary> levels = levelstoragesource.getLevelList();
        if (levels.stream().noneMatch(e -> e.getName().equals(folderName))) throw new RuntimeException("Level Not Found!");

        mc.execute(() -> {
            boolean bl = mc.isInSingleplayer();
            if (mc.world != null) mc.world.disconnect();
            if (bl) {
                mc.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
            } else {
                mc.disconnect();
            }
            mc.method_29970(new SaveLevelScreen(new TranslatableText("selectWorld.data_read")));
            mc.startIntegratedServer(folderName);
        });
    }
    
    /**
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @see #connect(String, int)
     *
     * @param ip
     */
    public void connect(String ip) {
        ServerAddress a = ServerAddress.parse(ip);
        connect(a.getAddress(), a.getPort());
    }
    
    /**
     * Connect to a server
     *
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @param ip
     * @param port
     */
    public void connect(String ip, int port) {
        mc.execute(() -> {
            boolean bl = mc.isInSingleplayer();
            if (mc.world != null) mc.world.disconnect();
            if (bl) {
                mc.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
            } else {
                mc.disconnect();
            }
            mc.openScreen(new ConnectScreen(null, mc, ip, port));
        });
    }
    
    /**
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * @see #disconnect(MethodWrapper)
     */
    public void disconnect() {
        disconnect(null);
    }
    
    /**
     * Disconnect from a server with callback.
     *
     * @since 1.2.3 (was in the {@code jsmacros} library until 1.2.9)
     *
     * {@code callback} defaults to {@code null}
     *
     * @param callback calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link java.lang.Boolean Boolean}&gt;
     */
    public void disconnect(MethodWrapper<Boolean, Object, Object, ?> callback) {
        mc.execute(() -> {
            boolean isWorld = mc.world != null;
            if (isWorld) {
                // logic in death screen disconnect button
                if (mc.world != null) mc.world.disconnect();
                mc.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                mc.openScreen(new TitleScreen());
            }
            try {
                if (callback != null)
                    callback.accept(isWorld);
            } catch (Throwable e) {
                Core.getInstance().profile.logError(e);
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
    public void shutdown() {
        mc.execute(mc::scheduleStop);

        if (!Core.getInstance().profile.checkJoinedThreadStack()) {
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
     * @since 1.2.4
     *
     * @see #waitTick(int)
     *
     * @throws InterruptedException
     */
    public void waitTick() throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }
        ctx.wrapSleep(tickSynchronizer::waitTick);
    }
    
    /**
     * waits the specified number of client ticks.
     * don't use this on an event that the main thread waits on (joins)... that'll cause circular waiting.
     * @since 1.2.6
     *
     * @param i
     * @throws InterruptedException
     */
    public void waitTick(int i) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }
        ctx.wrapSleep(() -> {
            int i2 = i;
            while (--i2 >= 0) {
                tickSynchronizer.waitTick();
            }
        });
    }

    /**
     * @param ip
     *
     * @return
     *
     * @since 1.6.5
     * @throws UnknownHostException
     * @throws InterruptedException
     */
    public ServerInfoHelper ping(String ip) throws UnknownHostException, InterruptedException {
        ServerInfo info = new ServerInfo("", ip, false);
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("pinging from main thread is not supported!");
        }
        Semaphore semaphore = new Semaphore(0);
        TickBasedEvents.serverListPinger.add(info, semaphore::release);
        semaphore.acquire();
        return new ServerInfoHelper(info);
    }

    /**
     * @param ip
     * @param callback
     *
     * @since 1.6.5
     * @throws UnknownHostException
     */
    public void pingAsync(String ip, MethodWrapper<ServerInfoHelper, IOException, Object, ?> callback) {
        CompletableFuture.runAsync(() -> {
            ServerInfo info = new ServerInfo("", ip, false);
            try {
                TickBasedEvents.serverListPinger.add(info, () -> callback.accept(new ServerInfoHelper(info), null));
            } catch (IOException e) {
                callback.accept(null , e);
            }
        });
    }

    /**
     * @since 1.6.5
     */
    public void cancelAllPings() {
        TickBasedEvents.serverListPinger.cancel();
    }

}
