package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.function.Consumer;

@Mixin(Minecraft.class)
abstract
class MixinMinecraftClient implements IMinecraftClient {

    @Shadow protected abstract void doUse();

    @Shadow protected abstract void doAttack();

    @Shadow public GuiScreen currentScreen;

    @Inject(at = @At("TAIL"), method = "func_71370_a")
    public void onResolutionChanged(CallbackInfo info) {

        synchronized (FHud.overlays) {
            for (IDraw2D<Draw2D> h : FHud.overlays) {
                try {
                    ((Draw2D) h).init();
                } catch (Throwable ignored) {}
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;removed()V"), method="openScreen")
    public void onCloseScreen(GuiScreen screen, CallbackInfo ci) {
        Consumer<IScreen> onClose = ((IScreen)currentScreen).getOnClose();
        try {
            if (onClose != null) onClose.accept((IScreen) currentScreen);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;removed()V", shift = At.Shift.AFTER), method = "openScreen", locals = LocalCapture.CAPTURE_FAILHARD)
    public void onGuiClose(GuiScreen guiScreenIn, CallbackInfo ci, GuiScreen  old) {
        if (old != null && ((IScreen) old).getOnClose() != null) {
            try {
                ((IScreen) old).getOnClose().accept((IScreen) old);
            } catch (Throwable e) {
                Core.getInstance().profile.logError(e);
            }
        }
    }

    @Override
    public void jsmacros_doItemUse() {
        doUse();
    }

    @Override
    public void jsmacros_doAttack() {
        doAttack();
    }


}
