package xyz.wagyourtail.jsmacros.mixins.events;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
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
import xyz.wagyourtail.jsmacros.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.api.helpers.BossBarHelper;
import xyz.wagyourtail.jsmacros.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.events.BlockUpdateCallback;
import xyz.wagyourtail.jsmacros.events.BossBarCallback;
import xyz.wagyourtail.jsmacros.events.ChunkLoadCallback;
import xyz.wagyourtail.jsmacros.events.ChunkUnloadCallback;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.ItemPickupCallback;
import xyz.wagyourtail.jsmacros.events.JoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerJoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerLeaveCallback;
import xyz.wagyourtail.jsmacros.events.TitleCallback;

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
        DeathCallback.EVENT.invoker().interact();
    }
    
    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
        if (this.client.isOnThread())
            switch (packet.getAction()) {
                case ADD_PLAYER:
                    for (Entry e : packet.getEntries()) {
                        if (playerListEntries.get(e.getProfile().getId()) == null) {
                            PlayerJoinCallback.EVENT.invoker().interact(e.getProfile().getId(), e.getProfile().getName());
                        }
                    }
                    return;
                case REMOVE_PLAYER:
                    for (Entry e : packet.getEntries()) {
                      if (playerListEntries.get(e.getProfile().getId()) != null) {
                            PlayerListEntry p = playerListEntries.get(e.getProfile().getId());
                            String name = null;
                            if (p != null) name = p.getProfile().getName();
                            PlayerLeaveCallback.EVENT.invoker().interact(e.getProfile().getId(), name);
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
        if (type != null) {
            TitleCallback.EVENT.invoker().interact(type, new TextHelper(packet.getText()));
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
        
        BossBarCallback.EVENT.invoker().interact(type, packet.getUuid().toString(), packet.getType() == BossBarS2CPacket.Type.REMOVE ? null : new BossBarHelper(((IBossBarHud) client.inGameHud.getBossBarHud()).jsmacros_GetBossBars().get(packet.getUuid())));
    }
    

    @Inject(at = @At(value="INVOKE", target="Lnet/minecraft/client/world/ClientWorld;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), method= "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
        Entity e = client.world.getEntityById(packet.getEntityId());
        LivingEntity c = (LivingEntity)client.world.getEntityById(packet.getCollectorEntityId());
        if (c == null) c = client.player;
        if (c.equals(client.player) && e instanceof ItemEntity) {
            ItemStackHelper item = new ItemStackHelper(((ItemEntity) e).getStack().copy());
            item.getRaw().setCount(packet.getStackAmount());
            ItemPickupCallback.EVENT.invoker().interact(item);
        }
    }
    
    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        JoinCallback.EVENT.invoker().interact(connection.getAddress().toString(), new ClientPlayerEntityHelper(client.player));
    }
    
    @Inject(at = @At("TAIL"), method="onChunkData")
    public void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        ChunkLoadCallback.EVENT.invoker().interact(packet.getX(), packet.getZ(), packet.isFullChunk());
    }
    
    @Inject(at = @At("TAIL"), method="onBlockUpdate")
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        BlockUpdateCallback.EVENT.invoker().interact(new BlockDataHelper(packet.getState(), world.getBlockEntity(packet.getPos()), packet.getPos()), "STATE");
    }
    
    @Inject(at = @At("TAIL"), method="onChunkDeltaUpdate")
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
        packet.visitUpdates((blockPos, blockState) -> {
            BlockUpdateCallback.EVENT.invoker().interact(new BlockDataHelper(blockState, world.getBlockEntity(blockPos), blockPos), "STATE");
        });
    }
    @Inject(at = @At("TAIL"), method="onBlockEntityUpdate")
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info) {
        BlockUpdateCallback.EVENT.invoker().interact(new BlockDataHelper(world.getBlockState(packet.getPos()), world.getBlockEntity(packet.getPos()), packet.getPos()), "ENTITY");
    }
    
    @Inject(at = @At("TAIL"), method="onUnloadChunk")
    public void onUnloadChunk(UnloadChunkS2CPacket packet, CallbackInfo info) {
        ChunkUnloadCallback.EVENT.invoker().interact(packet.getX(), packet.getZ());
    }
}

