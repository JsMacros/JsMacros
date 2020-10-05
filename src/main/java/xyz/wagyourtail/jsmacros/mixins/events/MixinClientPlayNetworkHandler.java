package xyz.wagyourtail.jsmacros.mixins.events;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import xyz.wagyourtail.jsmacros.access.IBossBarHud;
import xyz.wagyourtail.jsmacros.api.events.EventBlockUpdate;
import xyz.wagyourtail.jsmacros.api.events.EventBossbar;
import xyz.wagyourtail.jsmacros.api.events.EventChunkLoad;
import xyz.wagyourtail.jsmacros.api.events.EventChunkUnload;
import xyz.wagyourtail.jsmacros.api.events.EventDeath;
import xyz.wagyourtail.jsmacros.api.events.EventItemPickup;
import xyz.wagyourtail.jsmacros.api.events.EventJoinServer;
import xyz.wagyourtail.jsmacros.api.events.EventPlayerJoin;
import xyz.wagyourtail.jsmacros.api.events.EventPlayerLeave;
import xyz.wagyourtail.jsmacros.api.events.EventTitle;

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
    
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"), method="onCombatEvent", cancellable = true)
    private void onDeath(final CombatEventS2CPacket packet, CallbackInfo info) {
        new EventDeath();
    }
    
    @Unique
    Set<UUID> newPlayerEntries = new HashSet<>();
    
    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
        if (this.client.isOnThread())
            switch (packet.getAction()) {
                case ADD_PLAYER:
                    for (Entry e : packet.getEntries()) {
                        synchronized (newPlayerEntries) {
                            if (playerListEntries.get(e.getProfile().getId()) == null) {
                                newPlayerEntries.add(e.getProfile().getId());
                            }
                        }
                    }
                    return;
                case REMOVE_PLAYER:
                    for (Entry e : packet.getEntries()) {
                      if (playerListEntries.get(e.getProfile().getId()) != null) {
                            PlayerListEntry p = playerListEntries.get(e.getProfile().getId());
                            new EventPlayerLeave(e.getProfile().getId(), p);
                      }
                    }
                    return;
                default:
                    return;
            }
    }
    
    @Inject(at = @At("TAIL"), method = "onPlayerList")
    public void onPlayerListEnd(PlayerListS2CPacket packet, CallbackInfo info) {
        switch (packet.getAction()) {
            case ADD_PLAYER:
                for (Entry e : packet.getEntries()) {
                    synchronized (newPlayerEntries) {
                        if (newPlayerEntries.contains(e.getProfile().getId())) {
                            new EventPlayerJoin(e.getProfile().getId(), playerListEntries.get(e.getProfile().getId()));
                            newPlayerEntries.remove(e.getProfile().getId());
                        }
                    }
                }
                return;
            default:
                return;
        }
    }
    
    @Inject(at = @At("HEAD"), method = "onTitle")
    public void onTitle(TitleS2CPacket packet, CallbackInfo info) {
        String type = null;
        switch(packet.getAction()) {
            case TITLE:
                type = "TITLE";
                break;
            case SUBTITLE:
                type = "SUBTITLE";
                break;
            case ACTIONBAR:
                type = "ACTIONBAR";
                break;
            default:
                break;
        }
        if (type != null && packet.getText() != null) {
            new EventTitle(type, packet.getText());
        }
    }
    
    @Inject(at = @At("TAIL"), method="onBossBar")
    public void onBossBar(BossBarS2CPacket packet, CallbackInfo info) {
        String type = null;
        switch(packet.getType()) {
        case ADD:
            type = "ADD";
            break;
        case REMOVE:
            type = "REMOVE";
            break;
        case UPDATE_NAME:
            type = "UPDATE_NAME";
            break;
        case UPDATE_PCT:
            type = "UPDATE_PERCENT";
            break;
        case UPDATE_PROPERTIES:
            type = "UPDATE_PROPERTIES";
            break;
        case UPDATE_STYLE:
            type = "UPDATE_STYLE";
            break;
        default:
            break;
        }
        ClientBossBar bossBar = packet.getType() == BossBarS2CPacket.Type.REMOVE ? (ClientBossBar) null : 
            ((IBossBarHud) client.inGameHud.getBossBarHud()).jsmacros_GetBossBars().get(packet.getUuid());
        new EventBossbar(type, packet.getUuid(), bossBar);
    }
    

    @Inject(at = @At(value="INVOKE", target="Lnet/minecraft/client/world/ClientWorld;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), method= "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
        Entity e = client.world.getEntityById(packet.getEntityId());
        LivingEntity c = (LivingEntity)client.world.getEntityById(packet.getCollectorEntityId());
        if (c == null) c = client.player;
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
        new EventChunkLoad(packet.getX(), packet.getZ(), packet.isFullChunk());
    }
    
    @Inject(at = @At("TAIL"), method="onBlockUpdate")
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(packet.getState(), world.getBlockEntity(packet.getPos()), packet.getPos(), "STATE");
    }
    
    @Inject(at = @At("TAIL"), method="onChunkDeltaUpdate")
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
        packet.visitUpdates((blockPos, blockState) -> {
            new EventBlockUpdate(blockState, world.getBlockEntity(blockPos), blockPos, "STATE");
        });
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

