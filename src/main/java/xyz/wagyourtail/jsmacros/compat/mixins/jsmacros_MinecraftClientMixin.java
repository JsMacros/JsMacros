package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import xyz.wagyourtail.jsmacros.compat.interfaces.IMinecraftClient;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(MinecraftClient.class)
class jsmacros_MinecraftClientMixin implements IMinecraftClient {

    @Shadow
    private ClientConnection connection;

    @Inject(at = @At("TAIL"), method = "onResolutionChanged")
    public void jsmacros_onResolutionChanged(CallbackInfo info) {


        for (Draw2D h : ImmutableList.copyOf(hudFunctions.overlays)) {
            try {
                h.init();
            } catch (Exception e) {}
        }
    }

    @Override
    public ClientConnection getConnection() {
        return connection;
    }
}
