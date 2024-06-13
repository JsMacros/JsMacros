package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

import java.lang.reflect.Constructor;

@Mixin(value = GameRenderer.class)
public class MixinGameRenderer {

    private static final Constructor<DrawContext> DRAW_CONTEXT_CONSTRUCTOR;

    static {
        try {
            DRAW_CONTEXT_CONSTRUCTOR = DrawContext.class.getDeclaredConstructor(MinecraftClient.class, MatrixStack.class, VertexConsumerProvider.Immediate.class);
            DRAW_CONTEXT_CONSTRUCTOR.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void onRender(Screen instance, DrawContext drawContext, int mouseX, int mouseY, float delta) {
        instance.renderWithTooltip(drawContext, mouseX, mouseY, delta);
        if (!(client.currentScreen instanceof ScriptScreen)) {
            ((IScreenInternal) instance).jsmacros_render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"), method = "renderWorld")
    public void render(float tickDelta, long limitTime, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2) {
        client.getProfiler().swap("jsmacros_draw3d");
        MatrixStack ms = new MatrixStack();
        ms.multiplyPositionMatrix(matrix4f2);
        try {
            DrawContext drawContext = DRAW_CONTEXT_CONSTRUCTOR.newInstance(client, ms, client.getBufferBuilders().getEntityVertexConsumers());
            for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
                try {
                    d.render(drawContext, tickDelta);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        client.getProfiler().pop();
    }

    @Inject(at = @At("HEAD"), method = "updateCrosshairTarget", cancellable = true)
    public void onTargetUpdate(float tickDelta, CallbackInfo ci) {
        InteractionProxy.Target.onUpdate(tickDelta, ci);
    }

}
