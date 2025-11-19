package xyz.wagyourtail.jsmacros.fabric.client.mixins.access;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

@Mixin(value = WorldRenderer.class)
public class MixinWorldRenderer {

    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;
    @Shadow
    @Final
    private DefaultFramebufferSet framebufferSet;

    @Inject(method = "renderMain", at = @At("TAIL"))
    private void onRenderMain(FrameGraphBuilder frameGraphBuilder,
                              Frustum frustum,
                              Camera camera,
                              Matrix4f positionMatrix,
                              GpuBufferSlice fog,
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
