package xyz.wagyourtail.jsmacros.client.api.render.components3d;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.doclet.DocletIgnore;

public interface RenderElement3D<T extends RenderElement3D<?>> extends Comparable<RenderElement3D<?>> {

    @DocletIgnore
    void render(DrawContext drawContext, float tickDelta);

    @Override
    default int compareTo(@NotNull RenderElement3D o) {
        int i = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
        if (i == 0) {
            i = this.compareToSame((T) o);
        }
        return i;
    }

    int compareToSame(T other);

}
