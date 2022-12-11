package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

@Mixin(value = GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void onRender(Screen instance, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        instance.renderWithTooltip(matrices, mouseX, mouseY, delta);
        if (!(client.currentScreen instanceof ScriptScreen)) {
            ((IScreenInternal) instance).jsmacros_render(matrices, mouseX, mouseY, delta);
        }
    }
    
    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"), method = "renderWorld")
    public void render(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        client.getProfiler().swap("jsmacros_draw3d");
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                d.render(matrix, tickDelta);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        client.getProfiler().pop();
    }
}