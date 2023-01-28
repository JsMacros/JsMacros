package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface MixinWorldRenderer {

    @Invoker
    void invokeLoadTransparencyShader();
}
