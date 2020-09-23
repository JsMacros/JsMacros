package xyz.wagyourtail.jsmacros.mixins.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(MinecraftClient.class)
class MixinMinecraftClient {

    @Shadow
    private ClientConnection connection;

    @Inject(at = @At("TAIL"), method = "onResolutionChanged")
    public void onResolutionChanged(CallbackInfo info) {


        for (Draw2D h : ImmutableList.copyOf(hudFunctions.overlays)) {
            try {
                h.init();
            } catch (Exception e) {}
        }
    }
}
