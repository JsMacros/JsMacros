package xyz.wagyourtail.jsmacros.client.access;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

public interface IPacketHandler<T extends PacketListener> {
    Object2IntMap<Class<? extends Packet<T>>> getPacketIds();
}
