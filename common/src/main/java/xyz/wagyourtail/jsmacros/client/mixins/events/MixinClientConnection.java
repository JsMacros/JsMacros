package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @Shadow
    private Channel channel;
    @Unique
    private EventJoinedRecvPacket eventRecvPacket;
    @Unique
    private EventJoinedSendPacket eventSendPacket;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (!channel.isOpen()) {
            return;
        }
        EventJoinedRecvPacket event = new EventJoinedRecvPacket(packet);
        if (event.isCanceled() || event.packet == null) {
            ci.cancel();
        }
        eventRecvPacket = event;
        new EventRecvPacket(event.packet);
    }

    @ModifyArg(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V"), index = 0)
    public Packet<?> modifyReceivedPacket(Packet<?> packet) {
        return eventRecvPacket.packet;
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        EventJoinedSendPacket event = new EventJoinedSendPacket(packet);
        if (event.isCanceled() || event.packet == null) {
            ci.cancel();
        }
        eventSendPacket = event;
        new EventSendPacket(event.packet);
    }

    @ModifyArg(method = "sendImmediately", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;sendInternal(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;Lnet/minecraft/network/NetworkState;Lnet/minecraft/network/NetworkState;)V"), index = 0)
    public Packet<?> modifySendPacket(Packet<?> packet) {
        return eventSendPacket.packet;
    }

}
