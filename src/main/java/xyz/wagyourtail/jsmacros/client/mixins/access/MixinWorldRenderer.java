package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state);

    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Shadow
    @Nullable
    private ClientWorld world;

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=outline"), method = "render")
    public void renderInvisibleOutline(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        if (world == null) return;
        BlockHitResult target = (BlockHitResult) client.crosshairTarget;
        if (target == null) return;

        BlockPos pos = target.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!state.isAir() && !state.getOutlineShape(world, pos).isEmpty()) return;

        Vec3d campos = camera.getPos();
        drawBlockOutline(
                matrices,
                bufferBuilders.getEntityVertexConsumers().getBuffer(RenderLayer.getLines()),
                camera.getFocusedEntity(),
                campos.getX(), campos.getY(), campos.getZ(),
                pos, Blocks.STONE.getDefaultState() // stone won't ever change its shape, right?
        );
    }

}
