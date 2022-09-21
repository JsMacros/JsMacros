package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventJoinedRecvPacket;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventJoinedSendPacket;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRecvPacket;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSendPacket;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void logReceivedPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        EventJoinedRecvPacket event = new EventJoinedRecvPacket(packet);
        if (event.isCanceled()) {
            ci.cancel();
        }
        packet = event.packet;
        new EventRecvPacket(packet);
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void logSentPacket(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        EventJoinedSendPacket event = new EventJoinedSendPacket(packet);
        if (event.isCanceled()) {
            ci.cancel();
        }
        packet = event.packet;
        new EventSendPacket(packet);
    }

}
