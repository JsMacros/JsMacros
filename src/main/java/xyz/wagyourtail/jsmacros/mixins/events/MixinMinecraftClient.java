package xyz.wagyourtail.jsmacros.mixins.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.events.DimensionChangeCallback;
import xyz.wagyourtail.jsmacros.events.DisconnectCallback;
import xyz.wagyourtail.jsmacros.events.OpenScreenCallback;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    public Screen currentScreen;
    
    @Inject(at = @At("HEAD"), method="joinWorld")
    public void onJoinWorld(ClientWorld world, CallbackInfo info) {
        if (world != null)
            DimensionChangeCallback.EVENT.invoker().interact(world.getRegistryKey().getValue().toString());
    }
    
    @Inject(at = @At("HEAD"), method="openScreen")
    public void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen != currentScreen) OpenScreenCallback.EVENT.invoker().interact(jsMacros.getScreenName(screen));
    }
    
    @Inject(at = @At("TAIL"), method="disconnect")
    public void onDisconnect(CallbackInfo info) {
        DisconnectCallback.EVENT.invoker().interact();
    }
}
