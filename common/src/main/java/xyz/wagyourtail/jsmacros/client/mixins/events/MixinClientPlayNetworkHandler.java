package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.BossBarConsumer;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
class MixinClientPlayNetworkHandler {

    @Shadow
    private MinecraftClient client;
    @Shadow
    private ClientWorld world;
    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow
    @Final
    private Map<UUID, PlayerListEntry> playerListEntries;


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"), method="onDeathMessage", cancellable = true)
    private void onDeath(DeathMessageS2CPacket packet, CallbackInfo info) {
        new EventDeath();
    }

    @Unique
    private final Set<UUID> newPlayerEntries = new HashSet<>();

    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
        if (this.client.isOnThread()) {
            PlayerListS2CPacket.Action action = packet.getAction();
            if (action == PlayerListS2CPacket.Action.ADD_PLAYER) {
                for (Entry e : packet.getEntries()) {
                    synchronized (newPlayerEntries) {
                        if (playerListEntries.get(e.getProfile().getId()) == null) {
                            newPlayerEntries.add(e.getProfile().getId());
                        }
                    }
                }
            } else if (action == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
                for (Entry e : packet.getEntries()) {
                    if (playerListEntries.get(e.getProfile().getId()) != null) {
                        PlayerListEntry p = playerListEntries.get(e.getProfile().getId());
                        new EventPlayerLeave(e.getProfile().getId(), p);
                    }
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "onPlayerList")
    public void onPlayerListEnd(PlayerListS2CPacket packet, CallbackInfo info) {
        if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
            for (Entry e : packet.getEntries()) {
                synchronized (newPlayerEntries) {
                    if (newPlayerEntries.contains(e.getProfile().getId())) {
                        new EventPlayerJoin(e.getProfile().getId(), playerListEntries.get(e.getProfile().getId()));
                        newPlayerEntries.remove(e.getProfile().getId());
                    }
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "onTitle")
    public void onTitle(TitleS2CPacket packet, CallbackInfo info) {
        if (packet.getTitle() != null)
            new EventTitle("TITLE", packet.getTitle());
    }

    @Inject(at = @At("TAIL"), method = "onSubtitle")
    public void onSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
        if (packet.getSubtitle() != null)
            new EventTitle("SUBTITLE", packet.getSubtitle());
    }

    @Inject(at = @At("TAIL"), method = "onOverlayMessage")
    public void onOverlayMessage(OverlayMessageS2CPacket packet, CallbackInfo ci) {
        if (packet.getMessage() != null)
            new EventTitle("ACTIONBAR", packet.getMessage());
    }

    @Inject(at = @At("TAIL"), method="onBossBar")
    public void onBossBar(BossBarS2CPacket packet, CallbackInfo info) {
        packet.accept(new BossBarConsumer());
    }


    @Inject(at = @At(value="INVOKE", target="Lnet/minecraft/client/world/ClientWorld;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), method= "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
        assert client.world != null;
        final Entity e = client.world.getEntityById(packet.getEntityId());
        LivingEntity c = (LivingEntity)client.world.getEntityById(packet.getCollectorEntityId());
        if (c == null) c = client.player;
        assert c != null;
        if (c.equals(client.player) && e instanceof ItemEntity) {
            ItemStack item = ((ItemEntity) e).getStack().copy();
            item.setCount(packet.getStackAmount());
            new EventItemPickup(item);
        }
    }

    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        new EventJoinServer(client.player, connection.getAddress().toString());
    }

    @Inject(at = @At("TAIL"), method="onChunkData")
    public void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        new EventChunkLoad(packet.getX(), packet.getZ(), true);
    }

    @Inject(at = @At("TAIL"), method="onBlockUpdate")
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(packet.getState(), world.getBlockEntity(packet.getPos()), packet.getPos(), "STATE");
    }

    @Inject(at = @At("TAIL"), method="onChunkDeltaUpdate")
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
        packet.visitUpdates((blockPos, blockState) -> new EventBlockUpdate(blockState, world.getBlockEntity(blockPos), new BlockPos(blockPos), "STATE"));
    }

    @Inject(at = @At("TAIL"), method="onBlockEntityUpdate")
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(world.getBlockState(packet.getPos()), world.getBlockEntity(packet.getPos()), packet.getPos(), "ENTITY");
    }

    @Inject(at = @At("TAIL"), method="onUnloadChunk")
    public void onUnloadChunk(UnloadChunkS2CPacket packet, CallbackInfo info) {
        new EventChunkUnload(packet.getX(), packet.getZ());
    }
}

