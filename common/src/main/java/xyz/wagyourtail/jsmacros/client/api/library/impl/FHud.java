package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.CustomImage;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Functions for displaying stuff in 2 to 3 dimensions
 * 
 * An instance of this class is passed to scripts as the {@code Hud} variable.
 * 
 * @since 1.0.5
 * 
 * @author Wagyourtail
 */
 @Library("Hud")
 @SuppressWarnings("unused")
public class FHud extends BaseLibrary {
    
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * Don't touch this here
     */
    public static final Set<IDraw2D<Draw2D>> overlays = ConcurrentHashMap.newKeySet();
    /**
     * Don't touch this here
     */
    public static final Set<Draw3D> renders = ConcurrentHashMap.newKeySet();
    
    /**
     * 
     * @see IScreen
     * 
     * @since 1.0.5
     * 
     * @param title
     * @param dirtBG boolean of whether to use a dirt background or not.
     * @return a new {@link IScreen IScreen} Object.
     */
    public ScriptScreen createScreen(String title, boolean dirtBG) {
        return new ScriptScreen(title, dirtBG);
    }
    
    /**
     * Opens a {@link IScreen IScreen} Object.
     * 
     * @since 1.0.5
     * 
     * @see IScreen
     * 
     * @param s
     */
    public void openScreen(IScreen s) {
        net.minecraft.client.gui.screen.Screen screen = (net.minecraft.client.gui.screen.Screen) s;
        mc.execute(() -> {
            mc.openScreen(screen);
        });
    }
    
    /**
     * 
     * @since 1.2.7
     * 
     * @see IScreen
     * 
     * @return the currently open Screen as an {@link IScreen IScreen}
     */
    public IScreen getOpenScreen() {
        return (IScreen) mc.currentScreen;
    }

    /**
     * @param width  the width of the canvas
     * @param height the height of the canvas
     * @return a {@link CustomImage} that can be used as a texture for screen backgrounds, rendering
     *         images, etc.
     *
     * @since 1.8.4
     */
    public CustomImage createTexture(int width, int height, String name) {
        return CustomImage.createWidget(width, height, name);
    }

    /**
     * @param path absolute path to an image file
     * @return a {@link CustomImage} that can be used as a texture for screen backgrounds, rendering
     *         images, etc.
     *
     * @since 1.8.4
     */
    public CustomImage createTexture(String path, String name) {
        return CustomImage.createWidget(path, name);
    }

    /**
     * @return an immutable Map of all registered custom textures.
     *
     * @since 1.8.4
     */
    public Map<String, CustomImage> getRegisteredTextures() {
        return ImmutableMap.copyOf(CustomImage.IMAGES);
    }
    
    /**
     * @return the current gui scale factor of minecraft.
     *
     * @since 1.8.4
     */
    public int getScaleFactor() {
        return mc.options.guiScale;
    }
    
    /**
     * 
     * @since 1.0.5, renamed from {@code getOpenScreen} in 1.2.7
     * 
     * @return The name of the currently open screen.
     */
    public String getOpenScreenName() {
        return JsMacros.getScreenName(mc.currentScreen);
    }
    
    /**
     * 
     * @since 1.1.2
     * 
     * @return a {@link java.lang.Boolean boolean} denoting if the currently open screen is a container. 
     */
    public boolean isContainer() {
        return mc.currentScreen instanceof ContainerScreen;
    }
    
    
    /**
     * @since 1.0.5
     * 
     * @see IDraw2D
     *
     * @return
     */
    public Draw2D createDraw2D() {
        return new Draw2D();
    }
    
    /**
     * @since 1.0.5
     * 
     * Registers an {@link IDraw2D IDraw2D} to be rendered.
     * @deprecated since 1.6.5, use {@link Draw2D#register()} instead.
     * @see IDraw2D
     * 
     * @param overlay
     */
    @Deprecated
    public void registerDraw2D(IDraw2D<Draw2D> overlay) {
        ((Draw2D) overlay).init();
        overlays.add(overlay);
    }
    
    /**
     * @since 1.0.5
     * 
     * Unregisters an {@link IDraw2D IDraw2D} to stop it being rendered.
     * @deprecated since 1.6.5, use {@link Draw2D#unregister()} instead.
     * @see IDraw2D
     * 
     * @param overlay
     */
     @Deprecated
    public void unregisterDraw2D(IDraw2D<Draw2D> overlay) {
        overlays.remove(overlay);
    }
    
    /**
     * @since 1.0.5
     * 
     * @see IDraw2D
     * 
     * @return A list of current {@link IDraw2D IDraw2Ds}.
     */
    public List<IDraw2D<Draw2D>> listDraw2Ds() {
        return ImmutableList.copyOf(overlays);
    }
    
    /**
     * @since 1.0.5
     * 
     * clears the Draw2D render list.
     * 
     * @see IDraw2D
     */
    public void clearDraw2Ds() {
        overlays.clear();
    }
    
    /**
     * @since 1.0.6
     * 
     * @see Draw3D
     * 
     * @return a new {@link Draw3D Draw3D}.
     */
    public Draw3D createDraw3D() {
        return new Draw3D();
    }
    
    /**
     * @since 1.0.6
     * 
     * Registers an {@link Draw3D Draw3D} to be rendered.
     * @deprecated since 1.6.5 use {@link Draw3D#register()} instead.
     * @see Draw3D
     * 
     * @param draw
     */
     @Deprecated
    public void registerDraw3D(Draw3D draw) {
        renders.add(draw);
    }
    
    /**
     * @since 1.0.6
     * 
     * Unregisters an {@link Draw3D Draw3D} to stop it being rendered.
     * @since 1.6.5 use {@link Draw3D#unregister()} instead.
     * @see Draw3D
     * 
     * @param draw
     */
     @Deprecated
    public void unregisterDraw3D(Draw3D draw) {
        renders.remove(draw);
    }
    
    /**
     * @since 1.0.6
     * 
     * @see Draw3D
     * 
     * @return A list of current {@link Draw3D Draw3D}.
     */
    public List<Draw3D> listDraw3Ds() {
        return ImmutableList.copyOf(renders);
    }
    
    /**
     * @since 1.0.6
     * 
     * clears the Draw2D render list.
     * 
     * @see Draw3D
     */
    public void clearDraw3Ds() {
        renders.clear();
    }
    
    /**
     * @since 1.1.3
     * 
     * @return the current X coordinate of the mouse
     */
    public double getMouseX() {
        return mc.mouse.getX() * (double)mc.window.getScaledWidth() / (double)mc.window.getWidth();
    }
    
    /**
     * @since 1.1.3
     * 
     * @return the current Y coordinate of the mouse
     */
    public double getMouseY() {
        return mc.mouse.getY() * (double)mc.window.getScaledHeight() / (double)mc.window.getHeight();
    }

    /**
     * @return the current window width.
     *
     * @since 1.8.4
     */
    public int getWindowWidth() {
        return mc.getWindow().getWidth();
    }

    /**
     * @return the current window height.
     *
     * @since 1.8.4
     */
    public int getWindowHeight() {
        return mc.getWindow().getHeight();
    }
    
}