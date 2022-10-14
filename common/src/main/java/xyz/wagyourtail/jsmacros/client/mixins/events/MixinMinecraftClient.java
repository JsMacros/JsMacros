package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDimensionChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventOpenContainer;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventOpenScreen;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public GuiScreen currentScreen;

    @Shadow public abstract void openScreen(GuiScreen screen);

    @Shadow public EntityPlayerSP player;

    @Shadow public PlayerControllerMP interactionManager;

    @Shadow private static Minecraft instance;

    @Inject(at = @At("HEAD"), method="connect(Lnet/minecraft/client/multiplayer/WorldClient;)V")
    public void onJoinWorld(WorldClient world, CallbackInfo info) {
        if (world != null)
            new EventDimensionChange(world.getLevelProperties().getLevelName());
    }

    @Unique
    private GuiScreen prevScreen;
    
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", opcode = Opcodes.PUTFIELD), method="openScreen")
    public void onOpenScreen(GuiScreen screen, CallbackInfo info) {
        if (screen != currentScreen) {
            if (screen instanceof InventoryEffectRenderer && interactionManager.hasCreativeInventory()) {
                if (!(screen instanceof GuiContainerCreative)) {
                    prevScreen = currentScreen;
                }
            } else {
                prevScreen = currentScreen;
            }
            new EventOpenScreen(screen);
        }
    }

    @Inject(at = @At("TAIL"), method = "openScreen")
    public void afterOpenScreen(GuiScreen screen, CallbackInfo info) {
        if (screen instanceof GuiContainer) {
            if (interactionManager.hasCreativeInventory() && !(screen instanceof GuiContainerCreative)) {
                return;
            }
            EventOpenContainer event = new EventOpenContainer((GuiContainer) screen);
            if (event.cancelled) {
                openScreen(prevScreen);
            }
        }
        prevScreen = null;
    }
}
