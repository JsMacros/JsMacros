package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

/**
 * @author Wagyourtail
 */
public interface RenderElement extends Drawable {

    MinecraftClient mc = MinecraftClient.getInstance();

    int getZIndex();

    default void render3D(int mouseX, int mouseY, float delta) {
        render(mouseX, mouseY, delta);
    }
}
