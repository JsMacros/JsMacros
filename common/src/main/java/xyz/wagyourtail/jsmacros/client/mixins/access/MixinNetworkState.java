package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.INetworkState;

import java.util.Map;

@Mixin(NetworkState.class)
public class MixinNetworkState implements INetworkState {

    @Shadow
    @Final
    private Map<NetworkSide, ?> packetHandlers;

    @Override
    public Map<NetworkSide, ?> getPacketHandlers() {
        return packetHandlers;
    }
}
