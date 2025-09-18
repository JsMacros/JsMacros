package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import xyz.wagyourtail.doclet.DocletIgnore;

/**
 * @author Wagyourtail
 */
public interface RenderElement extends Drawable {

    MinecraftClient mc = MinecraftClient.getInstance();

    int getZIndex();

    @DocletIgnore
    default void render3D(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        render(drawContext, mouseX, mouseY, delta);
    }

    @DocletIgnore
    default void setupMatrix(MatrixStack matrices, double x, double y, float scale, float rotation, double width, double height, boolean rotateAroundCenter) {
        matrices.translate((float) x, (float) y, 1);
        matrices.scale(scale, scale, 1);
        if (rotateAroundCenter) {
            matrices.translate((float) (width / 2), (float) (height / 2), 1);
        }
        matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
        if (rotateAroundCenter) {
            matrices.translate((float) (-width / 2), (float) (-height / 2), 1);
        }
        matrices.translate((float) -x, (float) -y, 1);
    }

}
