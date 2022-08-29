package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.network.NetworkSide;

import java.util.Map;

public interface INetworkState {
    Map<NetworkSide, ?> getPacketHandlers();
}
