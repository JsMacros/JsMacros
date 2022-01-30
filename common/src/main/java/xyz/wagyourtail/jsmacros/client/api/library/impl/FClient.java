package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.config.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.client.tick.TickSync;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

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
public class FClient extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Don't touch this plz xd.
     */
    public static TickSync tickSynchronizer = new TickSync();
    
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
            if (mc.world != null) mc.world.disconnect();
            mc.joinWorld(null);
            ConnectScreen.connect(null, mc, new ServerAddress(ip, port), null);
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
            if (isWorld) mc.world.disconnect();
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
        tickSynchronizer.waitTick();
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
        while (--i >= 0) {
            tickSynchronizer.waitTick();
        }
    }
}
