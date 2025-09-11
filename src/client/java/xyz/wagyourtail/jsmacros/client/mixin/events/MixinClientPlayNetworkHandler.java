package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.BossBarConsumer;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventTitle;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventContainerUpdate;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventItemPickup;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventSlotUpdate;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventDeath;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventStatusEffectUpdate;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.*;
import xyz.wagyourtail.jsmacros.client.api.helper.StatusEffectHelper;

import java.util.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler extends ClientCommonNetworkHandler {

    @Shadow
    private ClientWorld world;

    @Shadow
    @Final
    private Map<UUID, PlayerListEntry> playerListEntries;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"), method = "onDeathMessage")
    private void onDeath(DeathMessageS2CPacket packet, CallbackInfo info) {
        new EventDeath().trigger();
    }

    @Unique
    private final Set<UUID> newPlayerEntries = new HashSet<>();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/SocialInteractionsManager;setPlayerOnline(Lnet/minecraft/client/network/PlayerListEntry;)V"), method = "onPlayerList", locals = LocalCapture.CAPTURE_FAILHARD)
    public void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci, Iterator var2, Entry entry, PlayerListEntry playerListEntry) {
        new EventPlayerJoin(entry.profileId(), playerListEntry).trigger();
    }

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z", remap = false), method = "onPlayerRemove", locals = LocalCapture.CAPTURE_FAILHARD)
    public void onPlayerListEnd(PlayerRemoveS2CPacket packet, CallbackInfo ci, Iterator var2, UUID uUID, PlayerListEntry playerListEntry) {
        new EventPlayerLeave(uUID, playerListEntry).trigger();
    }

    @ModifyArg(method = "onTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setTitle(Lnet/minecraft/text/Text;)V"))
    public Text onTitle(Text title) {
        EventTitle et = new EventTitle("TITLE", title);
        et.trigger();
        if (et.message == null || et.isCanceled()) {
            return null;
        } else {
            return et.message.getRaw();
        }
    }

    @ModifyArg(method = "onSubtitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setSubtitle(Lnet/minecraft/text/Text;)V"))
    public Text onSubtitle(Text title) {
        EventTitle et = new EventTitle("SUBTITLE", title);
        et.trigger();
        if (et.message == null || et.isCanceled()) {
            return null;
        } else {
            return et.message.getRaw();
        }
    }

    @ModifyArg(method = "onOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setOverlayMessage(Lnet/minecraft/text/Text;Z)V"))
    public Text onOverlayMessage(Text title) {
        EventTitle et = new EventTitle("ACTIONBAR", title);
        et.trigger();
        if (et.message == null || et.isCanceled()) {
            return null;
        } else {
            return et.message.getRaw();
        }
    }

    @Inject(at = @At("TAIL"), method = "onBossBar")
    public void onBossBar(BossBarS2CPacket packet, CallbackInfo info) {
        packet.accept(new BossBarConsumer());
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), method = "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
        assert client.world != null;
        final Entity e = client.world.getEntityById(packet.getEntityId());
        LivingEntity c = (LivingEntity) client.world.getEntityById(packet.getCollectorEntityId());
        if (c == null) {
            c = client.player;
        }
        assert c != null;
        if (c.equals(client.player) && e instanceof ItemEntity) {
            ItemStack item = ((ItemEntity) e).getStack().copy();
            item.setCount(packet.getStackAmount());
            new EventItemPickup(item).trigger();
        }
    }

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        new EventJoinServer(client.player, connection.getAddress().toString()).trigger();
    }

    @Inject(at = @At("TAIL"), method = "onChunkData")
    public void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        new EventChunkLoad(packet.getChunkX(), packet.getChunkZ(), true).trigger();
    }

    @Inject(at = @At("TAIL"), method = "onBlockUpdate")
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(packet.getState(), world.getBlockEntity(packet.getPos()), packet.getPos(), "STATE").trigger();
    }

    @Inject(at = @At("TAIL"), method = "onChunkDeltaUpdate")
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
        packet.visitUpdates((blockPos, blockState) -> new EventBlockUpdate(blockState, world.getBlockEntity(blockPos), new BlockPos(blockPos), "STATE").trigger());
    }

    @Inject(at = @At("TAIL"), method = "onBlockEntityUpdate")
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info) {
        new EventBlockUpdate(world.getBlockState(packet.getPos()), world.getBlockEntity(packet.getPos()), packet.getPos(), "ENTITY").trigger();
    }

    @Inject(at = @At("TAIL"), method = "onUnloadChunk")
    public void onUnloadChunk(UnloadChunkS2CPacket packet, CallbackInfo info) {
        new EventChunkUnload(packet.pos().x, packet.pos().z).trigger();
    }

    @Inject(method = "onEntityStatusEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet, CallbackInfo info) {
        assert client.player != null;
        if (packet.getEntityId() == client.player.getId()) {
            StatusEffectInstance newEffect = new StatusEffectInstance(packet.getEffectId(), packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), null);
            StatusEffectInstance oldEffect = client.player.getStatusEffect(packet.getEffectId());
            new EventStatusEffectUpdate(oldEffect == null ? null : new StatusEffectHelper(oldEffect), new StatusEffectHelper(newEffect), true).trigger();
        }
    }

    @Inject(method = "onRemoveEntityStatusEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    public void onEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet, CallbackInfo info) {
        if (packet.getEntity(client.world) == client.player) {
            assert client.player != null;
            new EventStatusEffectUpdate(new StatusEffectHelper(client.player.getStatusEffect(packet.effect())), null, false).trigger();
        }
    }

    @Inject(method = "onSetCursorItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V"))
    public void onHeldSlotUpdate(SetCursorItemS2CPacket packet, CallbackInfo ci) {
        HandledScreen<?> screen;
        if (this.client.currentScreen instanceof HandledScreen<?>) {
            screen = (HandledScreen<?>) this.client.currentScreen;
        } else {
            screen = new InventoryScreen(this.client.player);
        }
        new EventSlotUpdate(screen, "HELD", -999, this.client.player.currentScreenHandler.getCursorStack(), packet.contents()).trigger();
    }

    @Inject(method = "onSetPlayerInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    public void onInventorySlotUpdate(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
        assert client.player != null;
        new EventSlotUpdate(new InventoryScreen(client.player), "INVENTORY", packet.slot(), this.client.player.playerScreenHandler.getSlot(packet.slot()).getStack(), packet.contents()).trigger();
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;setStackInSlot(IILnet/minecraft/item/ItemStack;)V"))
    public void onScreenSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        assert client.player != null;
        new EventSlotUpdate(new InventoryScreen(client.player), "INVENTORY", packet.getSlot(), this.client.player.playerScreenHandler.getSlot(packet.getSlot()).getStack(), packet.getStack()).trigger();
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setStackInSlot(IILnet/minecraft/item/ItemStack;)V"))
    public void onScreenSlotUpdate2(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        assert client.player != null;
        if (packet.getSyncId() == 0) {
            new EventSlotUpdate(new InventoryScreen(this.client.player), "INVENTORY", packet.getSlot(), this.client.player.currentScreenHandler.getSlot(packet.getSlot()).getStack(), packet.getStack()).trigger();
            return;
        } else if (this.client.currentScreen instanceof HandledScreen<?>) {
            if (packet.getSyncId() == ((HandledScreen<?>) this.client.currentScreen).getScreenHandler().syncId) {
                new EventSlotUpdate((HandledScreen<?>) this.client.currentScreen, "CONTAINER", packet.getSlot(), this.client.player.currentScreenHandler.getSlot(packet.getSlot()).getStack(), packet.getStack()).trigger();
                return;
            }
        }
        new EventSlotUpdate(new InventoryScreen(this.client.player), "UNKNOWN", packet.getSlot(), this.client.player.currentScreenHandler.getSlot(packet.getSlot()).getStack(), packet.getStack()).trigger();
    }

    @Inject(method = "onInventory", at = @At("TAIL"))
    public void onInventoryUpdate(InventoryS2CPacket packet, CallbackInfo ci) {
        if (packet.syncId() == 0) {
            assert client.player != null;
            new EventContainerUpdate(new InventoryScreen(client.player)).trigger();
        } else {
            if (this.client.currentScreen instanceof HandledScreen<?>) {
                new EventContainerUpdate((HandledScreen<?>) this.client.currentScreen).trigger();
            }
        }
    }



    protected MixinClientPlayNetworkHandler(MinecraftClient arg, ClientConnection arg2, ClientConnectionState arg3) {
        super(arg, arg2, arg3);
    }

}
