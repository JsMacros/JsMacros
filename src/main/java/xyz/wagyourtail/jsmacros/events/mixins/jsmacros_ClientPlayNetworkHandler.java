package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.HeldItemCallback;
import xyz.wagyourtail.jsmacros.events.PlayerJoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerLeaveCallback;
import xyz.wagyourtail.jsmacros.events.TitleCallback;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

@Mixin(ClientPlayNetworkHandler.class)
class jsmacros_ClientPlayNetworkHandler {
    
    @Shadow
    private MinecraftClient client;
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"), method="onCombatEvent", cancellable = true)
    private void jsmacros_onDeath(final CombatEventS2CPacket packet, CallbackInfo info) {
        DeathCallback.EVENT.invoker().interact();
    }
    
    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void jsmacros_onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
        switch (packet.getAction()) {
            case ADD_PLAYER:
                for (Entry e : packet.getEntries()) {
                    PlayerJoinCallback.EVENT.invoker().interact(e.getProfile().getId(), e.getProfile().getName());
                }
            case REMOVE_PLAYER:
                for (Entry e : packet.getEntries()) {
                    PlayerLeaveCallback.EVENT.invoker().interact(e.getProfile().getId(), e.getProfile().getName());
                }
            default:
                break;
        }
    }
    
    @Inject(at = @At("HEAD"), method = "onTitle")
    public void jsmacros_onTitle(TitleS2CPacket packet, CallbackInfo info) {
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
    
    @Inject(at = @At("HEAD"), method = "onHeldItemChange")
    public void jsmacros_onHeldItemChange(HeldItemChangeS2CPacket packet, CallbackInfo info) {
        if (PlayerInventory.isValidHotbarIndex(packet.getSlot())) {
            HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(client.player.inventory.main.get(packet.getSlot())));
        }
    }
}

