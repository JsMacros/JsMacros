package xyz.wagyourtail.jsmacros.forge.client.mixins;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

import java.lang.reflect.Constructor;

@Mixin(GameRenderer.class)
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

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"), method = "renderWorld")
    public void render(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        client.getProfiler().swap("jsmacros_draw3d");
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                DrawContext drawContext = DRAW_CONTEXT_CONSTRUCTOR.newInstance(client, matrix, client.getBufferBuilders().getEntityVertexConsumers());
                d.render(drawContext, tickDelta);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        client.getProfiler().pop();
    }
}
