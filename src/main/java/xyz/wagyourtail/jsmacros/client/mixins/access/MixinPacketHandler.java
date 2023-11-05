package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;

import java.util.function.Function;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(targets = "net.minecraft.network.NetworkState$InternalPacketHandler")
public class MixinPacketHandler<T extends PacketListener> {

    @Inject(method = "register", at = @At("HEAD"))
    private <P extends Packet<T>> void onRegister(Class<P> type, Function<PacketByteBuf, P> packetFactory, CallbackInfoReturnable<?> cir) {
        PacketByteBufferHelper.BUFFER_TO_PACKET.put(type, packetFactory);
    }

}
