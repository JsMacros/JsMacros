package xyz.wagyourtail.jsmacros.api.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Arm;

/**
 * @author Wagyourtail
 * @since 1.1.7
 */
public class OptionsHelper {
    private GameOptions options;
    private MinecraftClient mc = MinecraftClient.getInstance();
    private ResourcePackManager rpm = mc.getResourcePackManager();
    
    public OptionsHelper(GameOptions options) {
        this.options = options;
    }
    /**
     * @since 1.1.7
     * @return 0: off, 2: fancy
     */
    public int getCloudMode() {
        switch (options.getCloudRenderMode()) {
            case FANCY:
                return 2;
            case FAST:
                return 1;
            default:
                return 0;
        }
    }
    /**
     * @since 1.1.7
     * @param mode 0: off, 2: fancy 
     * @return
     */
    public OptionsHelper setCloudMode(int mode) {
        switch(mode) {
            case 2:
                options.cloudRenderMode = CloudRenderMode.FANCY;
                return this;
            case 1:
                options.cloudRenderMode = CloudRenderMode.FAST;
                return this;
            default:
                options.cloudRenderMode = CloudRenderMode.OFF;
                return this;
        }
    }
    /**
     * @since 1.1.7
     * @return
     */
    public int getGraphicsMode() {
        switch (options.graphicsMode) {
            case FABULOUS:
                return 2;
            case FANCY:
                return 1;
            default:
                return 0;
        }
    }
    /**
     * @since 1.1.7
     * @param mode 0: fast, 2: fabulous
     * @return
     */
    public OptionsHelper setGraphicsMode(int mode) {
        switch(mode) {
            case 2:
                options.graphicsMode = GraphicsMode.FABULOUS;
                return this;
            case 1:
                options.graphicsMode = GraphicsMode.FANCY;
                return this;
            default:
                options.graphicsMode = GraphicsMode.FAST;
                return this;
        }
    }
    /**
     * @since 1.1.7
     * @return list of names of resource packs.
     */
    public List<String> getResourcePacks() {
        return new ArrayList<String>(rpm.getNames());
    }
    
    /**
     * @since 1.2.0
     * @return list of names of enabled resource packs.
     */
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<String>(rpm.getEnabledNames());
    }
    
    /**
     * Set the enabled resource packs to the provided list.
     * 
     * @since 1.2.0
     * @param enabled
     * @return
     */
    public OptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = new ArrayList<String>(Arrays.asList(enabled).stream().distinct().collect(Collectors.toList()));
        List<String> currentRP = ImmutableList.copyOf(options.resourcePacks);
        rpm.setEnabledProfiles(en);
        options.resourcePacks.clear();
        options.incompatibleResourcePacks.clear();
        for (ResourcePackProfile p : rpm.getEnabledProfiles()) {
            if (!p.isPinned()) {
                options.resourcePacks.add(p.getName());
                if (!p.getCompatibility().isCompatible()) {
                    options.incompatibleResourcePacks.add(p.getName());
                }
            }
        }
        options.write();
        List<String> newRP = ImmutableList.copyOf(options.resourcePacks);
        if (!currentRP.equals(newRP)) {
            mc.reloadResources();
        }
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public boolean isRightHanded() {
        return options.mainArm == Arm.RIGHT;
    }
    
    /**
     * @since 1.1.7
     * @param val
     */
    public void setRightHanded(boolean val) {
        if (val) {
            options.mainArm = Arm.RIGHT;
        } else {
            options.mainArm = Arm.LEFT;
        }
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public double getFov() {
        return options.fov;
    }
    
    /**
     * @since 1.1.7
     * @param fov
     * @return
     */
    public OptionsHelper setFov(double fov) {
        options.fov = fov;
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public int getRenderDistance() {
        return options.viewDistance;
    }
    
    /**
     * @since 1.1.7
     * @param d
     */
    public void setRenderDistance(int d) {
        options.viewDistance = d;
    }
    
    /**
     * @since 1.2.6
     * @return
     */
    public int getWidth() {
        return mc.getWindow().getWidth();
    }
    
    /**
     * @since 1.2.6
     * @return
     */
    public int getHeight() {
        return mc.getWindow().getHeight();
    }
    
    /**
     * @since 1.2.6
     * @param w
     */
    public void setWidth(int w) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, win.getHeight());
    }
    
    /**
     * @since 1.2.6
     * @param h
     */
    public void setHeight(int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), win.getWidth(), h);
    }
    
    /**
     * @since 1.2.6
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, h);
    }
    
    public GameOptions getRaw() {
        return options;
    }
}
