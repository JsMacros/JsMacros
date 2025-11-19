package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventLaunchGame;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventOpenContainer;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventOpenScreen;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventDimensionChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventDisconnect;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinDisconnectedScreen;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public Screen currentScreen;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    @Final
    private Session session;

    @Inject(at = @At("HEAD"), method = "joinWorld")
    public void onJoinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        if (world != null) {
            new EventDimensionChange(world.getRegistryKey().getValue().toString()).trigger();
        }
    }

    @Unique
    private Screen jsmacros$prevScreen;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.PUTFIELD), method = "setScreen")
    public void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen != currentScreen) {
            assert interactionManager != null;
            jsmacros$prevScreen = currentScreen;
            new EventOpenScreen(screen).trigger();
        }
    }

    @Inject(at = @At("TAIL"), method = "setScreen")
    public void afterOpenScreen(Screen screen, CallbackInfo info) {
        if (screen instanceof HandledScreen<?>) {
            assert interactionManager != null;
            if (interactionManager.getCurrentGameMode().isCreative() && !(screen instanceof CreativeInventoryScreen)) {
                return;
            }
            EventOpenContainer event = new EventOpenContainer(((HandledScreen<?>) screen));
            event.trigger();
            if (event.isCanceled()) {
                setScreen(jsmacros$prevScreen);
            }
        }
        jsmacros$prevScreen = null;
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;integratedServerRunning:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V")
    public void onDisconnect(Screen s, boolean transferring, CallbackInfo ci) {
        if (s instanceof DisconnectedScreen) {
            new EventDisconnect(((MixinDisconnectedScreen) s).getInfo().reason()).trigger();
        } else {
            new EventDisconnect(null).trigger();
        }
    }

    @Inject(at = @At("HEAD"), method = "run")
    private void onStart(CallbackInfo ci) {
        new EventLaunchGame(this.session.getUsername()).trigger();
    }

}
