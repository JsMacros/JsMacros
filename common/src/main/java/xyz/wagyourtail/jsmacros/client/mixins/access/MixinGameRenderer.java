package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface MixinGameRenderer {

    @Accessor
    void setBlockOutlineEnabled(boolean isBlockOutlineEnabled);

    @Accessor
    boolean isBlockOutlineEnabled();

    @Accessor
    void setRenderingPanorama(boolean isRenderingPanorama);

    @Accessor
    boolean isRenderingPanorama();
}
