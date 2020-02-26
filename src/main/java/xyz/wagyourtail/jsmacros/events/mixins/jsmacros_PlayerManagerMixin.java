package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.wagyourtail.jsmacros.events.JoinCallback;

@Mixin(PlayerManager.class)
class jsmacros_PlayerManagerMixin {
    
    @Inject(at = @At("TAIL"), method= "onPlayerConnect")
    private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, CallbackInfo info) {
        JoinCallback.EVENT.invoker().interact(conn, player);
    }
}
