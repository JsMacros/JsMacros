package xyz.wagyourtail.jsmacros.client.mixins.access;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(NetworkState.class)
public interface MixinNetworkState {
    @Accessor
    Map<NetworkSide, MixinPacketHandler> getPacketHandlers();

    @Mixin(targets = "net.minecraft.network.NetworkState$PacketHandler")
    interface MixinPacketHandler {

        @Accessor
        Object2IntMap<Class<? extends Packet<?>>> getPacketIds();

    }
}
