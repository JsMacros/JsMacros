package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;

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
