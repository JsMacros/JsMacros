package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import xyz.wagyourtail.jsmacros.client.access.IBossBarHud;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventInventoryChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventItemPickup;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventDeath;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventStatusEffectUpdate;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.*;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;

import java.util.*;

@Mixin(ClientPlayNetworkHandler.class)
class MixinClientPlayNetworkHandler {

    @Final
    @Shadow
    private MinecraftClient client;
    @Shadow
    private ClientWorld clientWorld;

    @Shadow
    @Final
    private Map<UUID, PlayerListEntry> playerListEntries;


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), method="onCombatEvent")
    private void onDeath(CombatEventS2CPacket packet, CallbackInfo info) {
        new EventDeath();
    }

    @Unique
    private final Set<UUID> newPlayerEntries = new HashSet<>();

    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
        if (this.client.isOnThread())
            switch (packet.getAction()) {
                case ADD_PLAYER:
                    for (PlayerListS2CPacket.Entry e : packet.getEntries()) {
                        synchronized (newPlayerEntries) {
                            if (playerListEntries.get(e.getProfile().getId()) == null) {
                                newPlayerEntries.add(e.getProfile().getId());
                            }
                        }
                    }
                    return;
                case REMOVE_PLAYER:
                    for (PlayerListS2CPacket.Entry e : packet.getEntries()) {
                        if (playerListEntries.get(e.getProfile().getId()) != null) {
                            PlayerListEntry p = playerListEntries.get(e.getProfile().getId());
                            new EventPlayerLeave(e.getProfile().getId(), p);
                        }
                    }
                    return;
                default:
            }
    }

    @Inject(at = @At("TAIL"), method = "onPlayerList")
    public void onPlayerListEnd(PlayerListS2CPacket packet, CallbackInfo info) {
        if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
            for (PlayerListS2CPacket.Entry e : packet.getEntries()) {
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
    public void onTitle(TitleS2CPacket packet, CallbackInfo info) {
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

    @Inject(at = @At(value="INVOKE", target="Lnet/minecraft/client/world/ClientWorld;playSound(DDDLnet/minecraft/sound/Sound;Lnet/minecraft/client/sound/SoundCategory;FFZ)V"), method= "onChunkRenderDistanceCenter")
    public void onItemPickupAnimation(ChunkRenderDistanceCenterS2CPacket packet, CallbackInfo info) {
        assert clientWorld != null;
        final Entity e = clientWorld.getEntityById(packet.getChunkX());
        LivingEntity c = (LivingEntity) clientWorld.getEntityById(packet.getChunkZ());
        if (c == null) c = client.player;
        assert c != null;
        if (c.equals(client.player) && e instanceof ItemEntity) {
            ItemStack item = ((ItemEntity) e).getItemStack().copy();
            item.setCount(1);
            new EventItemPickup(item);
        }
    }

    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        new EventJoinServer(client.player, connection.getAddress().toString());
    }

    @Inject(at = @At("RETURN"), method="onBossBar")
    public void onChunkData(BossBarS2CPacket packet, CallbackInfo info) {
        if (packet.getDataSize() == 0) {
            new EventChunkUnload(packet.getChunkX(), packet.getCHunkZ());
        } else {
            new EventChunkLoad(packet.getChunkX(), packet.getCHunkZ(), packet.shouldLoad());
        }
    }

    @Inject(at = @At("TAIL"), method="onBlockUpdate")
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(packet.getState(), clientWorld.getBlockEntity(packet.getPos()), packet.getPos(), "STATE");
    }

    @Inject(at = @At("TAIL"), method="onChunkDeltaUpdate")
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
        for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord record : packet.getRecords()) {
            new EventBlockUpdate(record.getState(), clientWorld.getBlockEntity(record.getBlockPos()), record.getBlockPos(), "STATE");
        }
    }

    @Inject(at = @At("TAIL"), method="onBlockEntityUpdate")
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(clientWorld.getBlockState(packet.getPos()), clientWorld.getBlockEntity(packet.getPos()), packet.getPos(), "ENTITY");
    }

    @Inject(at = @At("TAIL"), method="onUnloadChunk")
    public void onUnloadChunk(UnloadChunkS2CPacket packet, CallbackInfo info) {
        new EventChunkUnload(packet.getX(), packet.getZ());
    }

    @Inject(method = "onEntityPotionEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet, CallbackInfo info) {
        if (packet.getEntityId() == client.player.getEntityId()) {
            StatusEffectInstance newEffect = new StatusEffectInstance(StatusEffect.byRawId(packet.getEffectId()), packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon());
            StatusEffectInstance oldEffect = client.player.getStatusEffect(StatusEffect.byRawId(packet.getEffectId()));
            new EventStatusEffectUpdate(oldEffect == null ? null : new StatusEffectHelper(oldEffect), new StatusEffectHelper(newEffect), true);
        }
    }

    @Inject(method = "onRemoveEntityEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    public void onEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet, CallbackInfo info) {
        if (packet.getEntity(client.world) == client.player) {
            new EventStatusEffectUpdate(new StatusEffectHelper(client.player.getStatusEffect(packet.getEffectType())), null, false);
        }
    }

    @Inject(method = "onContainerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    public void onScreenHandlerSlotUpdate(ContainerSlotUpdateS2CPacket packet, CallbackInfo info) {
        if (packet.getSyncId() != -2) {
            return;
        }
        new EventInventoryChange(Inventory.create(), new int[]{packet.getSlot()}, new ItemStackHelper(client.player.inventory.getInvStack(packet.getSlot())), new ItemStackHelper(packet.getItemStack()));
    }
    
}