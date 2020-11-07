package xyz.wagyourtail.jsmacros.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.api.functions.FHud;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IDraw2D;

@Mixin(MinecraftClient.class)
class MixinMinecraftClient {

    @Shadow
    private ClientConnection connection;

    @Inject(at = @At("TAIL"), method = "onResolutionChanged")
    public void onResolutionChanged(CallbackInfo info) {

        synchronized (FHud.overlays) {
            for (IDraw2D<Draw2D> h : FHud.overlays) {
                try {
                    h.init();
                } catch (Exception e) {}
            }
        }
    }
}
