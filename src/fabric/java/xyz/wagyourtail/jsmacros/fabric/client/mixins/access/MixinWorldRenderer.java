package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
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

@Mixin(value = WorldRenderer.class)
public class MixinWorldRenderer {

    @Shadow
    private BufferBuilderStorage bufferBuilders;
    @Shadow
    private DefaultFramebufferSet framebufferSet;

    @Inject(method = "renderMain", at = @At("TAIL"))
    private void onRenderMain(FrameGraphBuilder frameGraphBuilder,
                              Frustum frustum,
                              Camera camera,
                              Matrix4f positionMatrix,
                              Matrix4f projectionMatrix,
                              Fog fog,
                              boolean renderBlockOutline,
                              boolean renderEntityOutlines,
                              RenderTickCounter renderTickCounter,
                              Profiler profiler,
                              CallbackInfo ci) {
        if (this.framebufferSet == null) {
            return;
        }
        FramePass framePass = frameGraphBuilder.createPass("jsmacros_draw3d");
        DefaultFramebufferSet frameBufferSet = this.framebufferSet;
        frameBufferSet.mainFramebuffer = framePass.transfer(frameBufferSet.mainFramebuffer);

        framePass.setRenderer(() -> {
            profiler.push("jsmacros_d3d");

            try {
                VertexConsumerProvider.Immediate consumers = bufferBuilders.getEffectVertexConsumers();

                float tickDelta = renderTickCounter.getTickProgress(true);

                MatrixStack matrixStack = new MatrixStack();
                matrixStack.translate(camera.getPos().negate());
                matrixStack.push();
                for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
                    d.render(matrixStack, consumers, tickDelta);
                }
                matrixStack.pop();
                consumers.draw();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            profiler.pop();
        });
    }

}
