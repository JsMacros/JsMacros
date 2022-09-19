package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;

import io.netty.util.concurrent.GenericFutureListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventPacketRecv;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventPacketSend;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static void logReceivedPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        new EventPacketRecv(packet);
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void logSentPacket(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        new EventPacketSend(packet);
    }

}
