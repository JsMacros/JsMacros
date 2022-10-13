package xyz.wagyourtail.jsmacros.forge.client.mixins;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.forge.client.forgeevents.ForgeEvents;

@Mixin(ForgeIngameGui.class)
public class MixinForgeIngameGui {

    @Inject(method = "renderHUDText", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z"))
    private void onPreDebugDraw(int width, int height, MatrixStack mStack, CallbackInfo ci) {
        ForgeEvents.renderHudListener((ForgeIngameGui) (Object) this, mStack, 0, width, height);
    }

}
