package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public interface RenderElement3D extends Comparable<RenderElement3D> {

    void render(MatrixStack drawContext, BufferBuilder builder, float tickDelta);

    @Override
    default int compareTo(@NotNull RenderElement3D o) {
        return this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    }

}
