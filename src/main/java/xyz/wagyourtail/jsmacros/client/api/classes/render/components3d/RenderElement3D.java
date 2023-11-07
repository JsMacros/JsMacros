package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.doclet.DocletIgnore;

public interface RenderElement3D extends Comparable<RenderElement3D> {

    @DocletIgnore
    void render(MatrixStack drawContext, BufferBuilder builder, float tickDelta);

    @Override
    default int compareTo(@NotNull RenderElement3D o) {
        int i = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
        if (i == 0) {
            i = this.hashCode() - o.hashCode();
        }
        return i;
    }

}
