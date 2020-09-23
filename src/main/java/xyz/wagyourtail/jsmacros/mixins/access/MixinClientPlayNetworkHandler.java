package xyz.wagyourtail.jsmacros.mixins.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import xyz.wagyourtail.jsmacros.runscript.functions.worldFunctions;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Unique
    long lastServerTimeRecvTime = 0;
    
    @Unique
    long lastServerTimeRecvTick = 0;
    
    @Unique
    Object timeSync = new Object();
    
    
    @Inject(at = @At("HEAD"), method="onWorldTimeUpdate")
    public void onServerTime(WorldTimeUpdateS2CPacket packet, CallbackInfo info) {
        synchronized (timeSync) {
            long tick = packet.getTime();
            long time = System.currentTimeMillis();
            if (tick != lastServerTimeRecvTick) {
                long mspt = (time - lastServerTimeRecvTime) / (tick - lastServerTimeRecvTick);
                worldFunctions.serverTPS = mspt * 20;
            }
        }
    }
    
}
