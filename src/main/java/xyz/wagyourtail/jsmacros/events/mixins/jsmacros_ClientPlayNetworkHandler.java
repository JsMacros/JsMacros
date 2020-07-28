package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import xyz.wagyourtail.jsmacros.events.BossBarCallback;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.PlayerJoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerLeaveCallback;
import xyz.wagyourtail.jsmacros.events.TitleCallback;
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
    
    @Inject(at = @At("HEAD"), method="onBossBar")
    public void jsmacros_onBossBar(BossBarS2CPacket packet, CallbackInfo info) {
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
        String style = null;
        switch (packet.getOverlay()) {
        case NOTCHED_10:
            style = "NOTCHED_10";
            break;
        case NOTCHED_12:
            style = "NOTCHED_12";
            break;
        case NOTCHED_20:
            style = "NOTCHED_20";
            break;
        case NOTCHED_6:
            style = "NOTCHED_6";
            break;
        case PROGRESS:
            style = "PROGRESS";
            break;
        default:
            break;
        }
        String color = null;
        switch (packet.getColor()) {
        case BLUE:
            color = "BLUE";
            break;
        case GREEN:
            color = "GREEN";
            break;
        case PINK:
            color = "PINK";
            break;
        case PURPLE:
            color = "PURPLE";
            break;
        case RED:
            color = "RED";
            break;
        case WHITE:
            color = "WHITE";
            break;
        case YELLOW:
            color = "YELLOW";
            break;
        default:
            break;
        }
        
        
        BossBarCallback.EVENT.invoker().interact(type, packet.getUuid().toString(), style, color, new TextHelper(packet.getName()), packet.getPercent());
    }
}

