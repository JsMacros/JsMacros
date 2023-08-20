package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.function.Consumer;

@Mixin(MinecraftClient.class)
abstract
class MixinMinecraftClient implements IMinecraftClient {

    @Shadow
    @Final
    private FontManager fontManager;

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract boolean doAttack();

    @Shadow
    protected abstract void handleBlockBreaking(boolean breaking);

    @Shadow
    protected int attackCooldown;

    @Shadow
    public Screen currentScreen;

    @Shadow
    private Overlay overlay;

    @Shadow
    private volatile boolean paused;

    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    private int itemUseCooldown;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Inject(at = @At("TAIL"), method = "onResolutionChanged")
    public void onResolutionChanged(CallbackInfo info) {

        synchronized (FHud.overlays) {
            for (IDraw2D<Draw2D> h : FHud.overlays) {
                try {
                    ((Draw2D) h).init();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;removed()V"), method = "setScreen")
    public void onCloseScreen(Screen screen, CallbackInfo ci) {
        Consumer<IScreen> onClose = ((IScreen) currentScreen).getOnClose();
        try {
            if (onClose != null) {
                onClose.accept((IScreen) currentScreen);
            }
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Inject(at = @At("HEAD"), method = "joinWorld")
    public void onJoinWorld(ClientWorld world, CallbackInfo ci) {
        InteractionProxy.reset();
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;integratedServerRunning:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void onDisconnect(Screen s, CallbackInfo ci) {
        InteractionProxy.reset();
    }

    @Inject(at = @At("HEAD"), method = "handleBlockBreaking", cancellable = true)
    private void overrideBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (InteractionProxy.Break.isBreaking()) {
            if (options.attackKey.isPressed()) {
                InteractionProxy.Break.setOverride(false, "INTERRUPTED");
                return;
            }
            if (this.attackCooldown > 20) this.attackCooldown = 0;
            if (!breaking) {
                ci.cancel();
                handleBlockBreaking(true);
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"), method = "doAttack", cancellable = true)
    private void blockAttackBlock(CallbackInfoReturnable<Boolean> cir) {
        if (InteractionProxy.Break.isBreaking()) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=gameRenderer"), method = "tick")
    private void ensureOverrideInteractions(CallbackInfo ci) {
        if (!(overlay == null && currentScreen == null) && !paused) {
            if (InteractionProxy.Break.isBreaking()) {
                handleBlockBreaking(true);
                if (attackCooldown > 0) --attackCooldown;
            }
            InteractionProxy.Interact.ensureInteracting(itemUseCooldown);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V"), method = "handleInputEvents")
    private void continueInteracting(ClientPlayerInteractionManager im, PlayerEntity player) {
        if (!InteractionProxy.Interact.isInteracting()) im.stopUsingItem(player);
    }

    @Inject(at = @At("TAIL"), method = "handleInputEvents")
    private void ensureInteracting(CallbackInfo ci) {
        InteractionProxy.Interact.ensureInteracting(itemUseCooldown);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V"), method = "handleBlockBreaking")
    private void catchEmptyShapeException(ParticleManager pm, BlockPos pos, Direction dir) {
        if (world != null && !world.getBlockState(pos).getOutlineShape(world, pos).isEmpty()) pm.addBlockBreakingParticles(pos, dir);
    }

    @Override
    public FontManager jsmacros_getFontManager() {
        return fontManager;
    }

    @Override
    public void jsmacros_doItemUse() {
        doItemUse();
    }

    @Override
    public void jsmacros_doAttack() {
        doAttack();
    }

}
