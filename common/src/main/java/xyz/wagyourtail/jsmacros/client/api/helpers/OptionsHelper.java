package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.access.IResourcePackManager;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinSimpleOption;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.1.7
 */
@SuppressWarnings("unused")
public class OptionsHelper extends BaseHelper<GameOptions> {
    private static final Map<String, SoundCategory> SOUND_CATEGORY_MAP = Arrays.stream(SoundCategory.values()).collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ResourcePackManager rpm = mc.getResourcePackManager();
    
    public OptionsHelper(GameOptions options) {
        super(options);
    }
    /**
     * @since 1.1.7
     * @return 0: off, 2: fancy
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getCloudMode() {
        switch (base.getCloudRenderMode().getValue()) {
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
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setCloudMode(int mode) {
        switch(mode) {
            case 2:
                base.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
                return this;
            case 1:
                base.getCloudRenderMode().setValue(CloudRenderMode.FAST);
                return this;
            default:
                base.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                return this;
        }
    }
    /**
     * @since 1.1.7
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getGraphicsMode() {
        switch (base.getGraphicsMode().getValue()) {
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
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setGraphicsMode(int mode) {
        switch(mode) {
            case 2:
                base.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
                return this;
            case 1:
                base.getGraphicsMode().setValue(GraphicsMode.FANCY);
                return this;
            default:
                base.getGraphicsMode().setValue(GraphicsMode.FAST);
                return this;
        }
    }
    /**
     * @since 1.1.7
     * @return list of names of resource packs.
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public List<String> getResourcePacks() {
        return new ArrayList<String>(rpm.getNames());
    }
    
    /**
     * @since 1.2.0
     * @return list of names of enabled resource packs.
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<String>(rpm.getEnabledNames());
    }
    
    /**
     * Set the enabled resource packs to the provided list.
     * 
     * @since 1.2.0
     * @param enabled
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = new ArrayList<String>(Arrays.asList(enabled).stream().distinct().collect(Collectors.toList()));
        List<String> currentRP = ImmutableList.copyOf(base.resourcePacks);
        rpm.setEnabledProfiles(en);
        base.resourcePacks.clear();
        base.incompatibleResourcePacks.clear();
        for (ResourcePackProfile p : rpm.getEnabledProfiles()) {
            if (!p.isPinned()) {
                base.resourcePacks.add(p.getName());
                if (!p.getCompatibility().isCompatible()) {
                    base.incompatibleResourcePacks.add(p.getName());
                }
            }
        }
        base.write();
        List<String> newRP = ImmutableList.copyOf(base.resourcePacks);
        if (!currentRP.equals(newRP)) {
            mc.reloadResources();
        }
        return this;
    }

    /**
     * @since 1.8.3
     * @param state false to put it back
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper removeServerResourcePack(boolean state) {
        if (state != ((IResourcePackManager) rpm).jsmacros_isServerPacksDisabled()) {
            ((IResourcePackManager) rpm).jsmacros_disableServerPacks(state);
            mc.reloadResources();
        }
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public boolean isRightHanded() {
        return base.getMainArm().getValue() == Arm.RIGHT;
    }
    
    /**
     * @since 1.1.7
     * @param val
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setRightHanded(boolean val) {
        if (val) {
            base.getMainArm().setValue(Arm.RIGHT);
        } else {
            base.getMainArm().setValue(Arm.LEFT);
        }
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public double getFov() {
        return base.getFov().getValue();
    }
    
    /**
     * @since 1.1.7
     * @param fov (int since 1.7.0)
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setFov(int fov) {
        ((MixinSimpleOption)(Object) base.getFov()).forceSetValue(fov);
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getRenderDistance() {
        return base.getViewDistance().getValue();
    }
    
    /**
     * @since 1.1.7
     * @param d
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setRenderDistance(int d) {
        ((MixinSimpleOption)(Object) base.getViewDistance()).forceSetValue(d);
        return this;
    }
    
    /**
     * @since 1.2.6
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getWidth() {
        return mc.getWindow().getWidth();
    }
    
    /**
     * @since 1.2.6
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getHeight() {
        return mc.getWindow().getHeight();
    }
    
    /**
     * @since 1.2.6
     * @param w
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setWidth(int w) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, win.getHeight());
        return this;
    }
    
    /**
     * @since 1.2.6
     * @param h
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setHeight(int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), win.getWidth(), h);
        return this;
    }
    
    /**
     * @since 1.2.6
     * @param w
     * @param h
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setSize(int w, int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, h);
        return this;
    }
    
    /**
     * @since 1.3.0
     * normal values for gamam are between {@code 0} and {@code 1}
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public double getGamma() {
        return base.getGamma().getValue();
    }
    
    /**
     * @since 1.3.0
     * normal values for gamma are between {@code 0} and {@code 1}
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setGamma(double gamma) {
        ((MixinSimpleOption)(Object) base.getGamma()).forceSetValue(gamma);
        return this;
    }
    
    /**
     * @since 1.3.1
     * @param vol
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setVolume(double vol) {
        base.setSoundVolume(SoundCategory.MASTER, (float) vol);
        return this;
    }
    
    /**
     * set volume by category.
     *
     * @since 1.3.1
     * @param category
     * @param volume
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setVolume(String category, double volume) {
        base.setSoundVolume(SOUND_CATEGORY_MAP.get(category), (float) volume);
        return this;
    }
    
    /**
     * @since 1.3.1
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public Map<String, Float> getVolumes() {
        Map<String, Float> volumes = new HashMap<>();
        for (SoundCategory category : SoundCategory.values()) {
            volumes.put(category.getName(), base.getSoundVolume(category));
        }
        return volumes;
    }
    
    /**
     * sets gui scale, {@code 0} for auto.
     *
     * @since 1.3.1
     * @param scale
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setGuiScale(int scale) {
        base.getGuiScale().setValue(scale);
        mc.execute(mc::onResolutionChanged);
        return this;
    }
    
    /**
     * @since 1.3.1
     * @return gui scale, {@code 0} for auto.
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getGuiScale() {
        return base.getGuiScale().getValue();
    }
    
    /**
     * @param category
     * @since 1.3.1
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public float getVolume(String category) {
        return base.getSoundVolume(SOUND_CATEGORY_MAP.get(category));
    }

    /**
     * @since 1.5.0
     * @return
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public boolean getSmoothCamera() {
        return base.smoothCameraEnabled;
    }

    /**
     * @param val
     * @since 1.5.0
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setSmoothCamera(boolean val) {
        base.smoothCameraEnabled = val;
        return this;
    }

    /**
     * @since 1.5.0
     * @return 0 for 1st person, 2 for in front.
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public int getCameraMode() {
        return base.getPerspective().ordinal();
    }

    /**
     * @param mode 0: first, 2: front
     * @since 1.5.0
     * @deprecated use the respective method in {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FClient#getFullGameOptions()} instead.
     */
    @Deprecated
    public OptionsHelper setCameraMode(int mode) {
        base.setPerspective(Perspective.values()[mode]);
        return this;
    }
}
