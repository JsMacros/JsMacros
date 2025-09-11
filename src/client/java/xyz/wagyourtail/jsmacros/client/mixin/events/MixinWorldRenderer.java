package xyz.wagyourtail.jsmacros.client.mixin.events;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Shadow
    private DefaultFramebufferSet framebufferSet;

    @Inject(method = "renderMain", at = @At("TAIL"))
    private void onRenderMain(
            FrameGraphBuilder frameGraphBuilder,
            Frustum frustum,
            Camera camera,
            Matrix4f positionMatrix,
            GpuBufferSlice fog,
            boolean renderBlockOutline,
            boolean renderEntityOutline,
            RenderTickCounter tickCounter,
            Profiler profiler,
            CallbackInfo ci
    ) {
        if (this.framebufferSet == null) {
            return;
        }
        FramePass framePass = frameGraphBuilder.createPass("jsmacros_draw3d");
        DefaultFramebufferSet frameBufferSet = this.framebufferSet;
        frameBufferSet.mainFramebuffer = framePass.transfer(frameBufferSet.mainFramebuffer);

        framePass.setRenderer(() -> {
            profiler.push("jsmacros_d3d");

            try {
                VertexConsumerProvider.Immediate consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

                float tickDelta = tickCounter.getTickProgress(true);

                MatrixStack matrixStack = new MatrixStack();

                for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {

                    d.render(matrixStack, consumers, tickDelta);
                }

                consumers.draw();

            } catch (Throwable e) {
                e.printStackTrace();
            }

            profiler.pop();
        });
    }
}