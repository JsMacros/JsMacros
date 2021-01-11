package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.events.EventDimensionChange;
import xyz.wagyourtail.jsmacros.client.api.events.EventDisconnect;
import xyz.wagyourtail.jsmacros.client.api.events.EventOpenScreen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    public Screen currentScreen;
    
    @Inject(at = @At("HEAD"), method="joinWorld")
    public void onJoinWorld(ClientWorld world, CallbackInfo info) {
        if (world != null)
            new EventDimensionChange(world.getRegistryKey().getValue().toString());
    }
    
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.PUTFIELD), method="openScreen")
    public void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen != currentScreen) new EventOpenScreen(screen);
    }
    
    @Inject(at = @At("TAIL"), method="disconnect()V")
    public void onDisconnect(CallbackInfo info) {
        new EventDisconnect();
    }
}
