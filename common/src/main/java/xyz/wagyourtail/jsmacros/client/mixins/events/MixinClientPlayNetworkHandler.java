package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
class MixinClientPlayNetworkHandler {

    @Shadow
    private Minecraft client;
    @Shadow
    private WorldClient clientWorld;

    @Shadow
    @Final
    private Map<UUID, NetworkPlayerInfo> playerListEntries;


    @Shadow @Final private NetworkManager connection;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/MetadataPlayerDeath;setDescription(Ljava/lang/String;)V"), method="onCombatEvent")
    private void onDeath(final S42PacketCombatEvent packet, CallbackInfo info) {
        new EventDeath();
    }

    @Unique
    private final Set<UUID> newPlayerEntries = new HashSet<>();

    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void onPlayerList(S38PacketPlayerListItem packet, CallbackInfo info) {
        if (this.client.isOnThread())
            switch (packet.getAction()) {
                case ADD_PLAYER:
                    for (S38PacketPlayerListItem.AddPlayerData e : packet.getEntries()) {
                        synchronized (newPlayerEntries) {
                            if (playerListEntries.get(e.getProfile().getId()) == null) {
                                newPlayerEntries.add(e.getProfile().getId());
                            }
                        }
                    }
                    return;
                case REMOVE_PLAYER:
                    for (S38PacketPlayerListItem.AddPlayerData e : packet.getEntries()) {
                        if (playerListEntries.get(e.getProfile().getId()) != null) {
                            NetworkPlayerInfo p = playerListEntries.get(e.getProfile().getId());
                            new EventPlayerLeave(e.getProfile().getId(), p);
                        }
                    }
                    return;
                default:
            }
    }

    @Inject(at = @At("TAIL"), method = "onPlayerList")
    public void onPlayerListEnd(S38PacketPlayerListItem packet, CallbackInfo info) {
        if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
            for (S38PacketPlayerListItem.AddPlayerData e : packet.getEntries()) {
                synchronized (newPlayerEntries) {
                    if (newPlayerEntries.contains(e.getProfile().getId())) {
                        new EventPlayerJoin(e.getProfile().getId(), playerListEntries.get(e.getProfile().getId()));
                        newPlayerEntries.remove(e.getProfile().getId());
                    }
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "onTitle")
    public void onTitle(S45PacketTitle packet, CallbackInfo info) {
        String type = null;
        switch(packet.getAction()) {
            case TITLE:
                type = "TITLE";
                break;
            case SUBTITLE:
                type = "SUBTITLE";
                break;
            default:
                break;
        }
        if (type != null && packet.getText() != null) {
            new EventTitle(type, packet.getText());
        }
    }

    @Inject(at = @At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;playSound(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"), method= "onChunkRenderDistanceCenter")
    public void onItemPickupAnimation(S0DPacketCollectItem packet, CallbackInfo info) {
        assert clientWorld != null;
        final Entity e = clientWorld.getEntityById(packet.getChunkX());
        EntityLivingBase c = (EntityLivingBase) clientWorld.getEntityById(packet.getChunkZ());
        if (c == null) c = client.player;
        assert c != null;
        if (c.equals(client.player) && e instanceof EntityItem) {
            ItemStack item = ((EntityItem) e).getItemStack().copy();
            item.count = 1;
            new EventItemPickup(item);
        }
    }

    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void onGameJoin(S01PacketJoinGame packet, CallbackInfo info) {
        new EventJoinServer(client.player, connection.getAddress().toString());
    }

    @Inject(at = @At("RETURN"), method="onBossBar")
    public void onChunkData(S21PacketChunkData packet, CallbackInfo info) {
        if (packet.getDataSize() == 0) {
            new EventChunkUnload(packet.getChunkX(), packet.getCHunkZ());
        } else {
            new EventChunkLoad(packet.getChunkX(), packet.getCHunkZ(), packet.shouldLoad());
        }
    }

    @Inject(at = @At("TAIL"), method = "onChunkMap")
    public void onChunkDatas(S26PacketMapChunkBulk packet, CallbackInfo ci) {
        for (int i = 0; i < packet.getXPosLength(); ++i) {
            new EventChunkLoad(packet.getXPos(i), packet.getZPos(i), true);
        }
    }

    @Inject(at = @At("TAIL"), method="onBlockUpdate")
    public void onBlockUpdate(S23PacketBlockChange packet, CallbackInfo info) {
        new EventBlockUpdate(packet.state, clientWorld.getBlockEntity(packet.getPos()), packet.getPos(), "STATE");
    }

    @Inject(at = @At("TAIL"), method="onChunkDeltaUpdate")
    public void onChunkDeltaUpdate(S22PacketMultiBlockChange packet, CallbackInfo info) {
        for (S22PacketMultiBlockChange.BlockUpdateData record : packet.getRecords()) {
            new EventBlockUpdate(record.getState(), clientWorld.getBlockEntity(record.getBlockPos()), record.getBlockPos(), "STATE");
        }
    }

    @Inject(at = @At("TAIL"), method="onBlockEntityUpdate")
    public void onBlockEntityUpdate(S35PacketUpdateTileEntity packet, CallbackInfo info) {
        new EventBlockUpdate(clientWorld.getBlockState(packet.getPos()), clientWorld.getBlockEntity(packet.getPos()), packet.getPos(), "ENTITY");
    }
}

