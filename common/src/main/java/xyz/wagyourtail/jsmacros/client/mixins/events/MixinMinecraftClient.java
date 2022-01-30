package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDimensionChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDisconnect;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventOpenScreen;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinDisconnectedScreen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    public Screen currentScreen;

    @Inject(at = @At("HEAD"), method="joinWorld")
    public void onJoinWorld(ClientWorld world, CallbackInfo info) {
        if (world != null)
            new EventDimensionChange(world.getRegistryKey().getValue().toString());
    }
    
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.PUTFIELD), method="setScreen")
    public void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen != currentScreen) new EventOpenScreen(screen);
    }
    
    @Inject(at = @At(value = "INVOKE", target= "Lnet/minecraft/client/MinecraftClientGame;onLeaveGameSession()V"), method="disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void onDisconnect(Screen s, CallbackInfo info) {
        if (s instanceof DisconnectedScreen ds) {
            new EventDisconnect(((MixinDisconnectedScreen) s).getReason());
        } else {
            new EventDisconnect(null);
        }
    }
}
