package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.TPSData;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FWorld;

import java.util.LinkedList;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Unique
    long jsmacros$lastServerTimeRecvTime = 0;

    @Unique
    long jsmacros$lastServerTimeRecvTick = 0;

    @Unique
    List<TPSData> jsmacros$tpsData1M = new LinkedList<>();
    @Unique
    List<TPSData> jsmacros$tpsData5M = new LinkedList<>();
    @Unique
    List<TPSData> jsmacros$tpsData15M = new LinkedList<>();

    @Unique
    private final Object jsmacros$timeSync = new Object();

    @Inject(at = @At("HEAD"), method = "onWorldTimeUpdate")
    public void onServerTime(WorldTimeUpdateS2CPacket packet, CallbackInfo info) {
        synchronized (jsmacros$timeSync) {
            final long tick = packet.getTime();
            final long time = System.currentTimeMillis();
            if (tick != jsmacros$lastServerTimeRecvTick) {
                double mspt = (double) (time - jsmacros$lastServerTimeRecvTime) / (double) (tick - jsmacros$lastServerTimeRecvTick);
                // if just joined
                if (jsmacros$lastServerTimeRecvTick == 0) {
                    jsmacros$lastServerTimeRecvTime = time;
                    jsmacros$lastServerTimeRecvTick = tick;
                    return;
                }
                // if recorded more than 1000 ticks in a second, reset mspt to value for 25 tps
                // this is probably bed sleeping...
                if (mspt < 1) {
                    mspt = 40;
                }

                jsmacros$lastServerTimeRecvTime = time;
                jsmacros$lastServerTimeRecvTick = tick;

                FWorld.serverInstantTPS = 1000 / mspt;
                jsmacros$tpsData1M.add(new TPSData(time, FWorld.serverInstantTPS));
                if (time - jsmacros$tpsData1M.get(0).recvTime > 60000) {
                    jsmacros$tpsData1M.remove(0);
                }
                FWorld.server1MAverageTPS = jsmacros$tpsData1M.stream().reduce(0D, (res, data) -> res + data.tps, Double::sum) / (double) jsmacros$tpsData1M.size();
                if (jsmacros$tpsData5M.isEmpty() || time - jsmacros$tpsData5M.get(jsmacros$tpsData5M.size() - 1).recvTime > 60000) {
                    jsmacros$tpsData5M.add(new TPSData(time, FWorld.server1MAverageTPS));
                }
                if (time - jsmacros$tpsData5M.get(0).recvTime > 60000 * 5) {
                    jsmacros$tpsData5M.remove(0);
                }
                FWorld.server5MAverageTPS = jsmacros$tpsData5M.stream().reduce(0D, (res, data) -> res + data.tps, Double::sum) / (double) jsmacros$tpsData5M.size();
                if (jsmacros$tpsData15M.isEmpty() || time - jsmacros$tpsData15M.get(jsmacros$tpsData15M.size() - 1).recvTime > 60000 * 5) {
                    jsmacros$tpsData15M.add(new TPSData(time, FWorld.server5MAverageTPS));
                }
                if (time - jsmacros$tpsData15M.get(0).recvTime > 60000 * 15) {
                    jsmacros$tpsData15M.remove(0);
                }
                FWorld.server15MAverageTPS = jsmacros$tpsData15M.stream().reduce(0D, (res, data) -> res + data.tps, Double::sum) / (double) jsmacros$tpsData15M.size();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        synchronized (jsmacros$timeSync) {
            jsmacros$lastServerTimeRecvTime = 0;
            jsmacros$lastServerTimeRecvTick = 0;

            jsmacros$tpsData1M.clear();
            jsmacros$tpsData5M.clear();
            jsmacros$tpsData15M.clear();
        }
    }

}
