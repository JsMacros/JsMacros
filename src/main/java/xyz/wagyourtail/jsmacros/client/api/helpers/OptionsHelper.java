package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Arm;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.access.IResourcePackManager;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinSimpleOption;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class OptionsHelper extends BaseHelper<GameOptions> {

    private static final Map<String, SoundCategory> SOUND_CATEGORY_MAP = Arrays.stream(SoundCategory.values()).collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ResourcePackManager rpm = mc.getResourcePackManager();

    public final SkinOptionsHelper skin = new SkinOptionsHelper(this);
    public final VideoOptionsHelper video = new VideoOptionsHelper(this);
    public final MusicOptionsHelper music = new MusicOptionsHelper(this);
    public final ControlOptionsHelper control = new ControlOptionsHelper(this);
    public final ChatOptionsHelper chat = new ChatOptionsHelper(this);
    public final AccessibilityOptionsHelper accessibility = new AccessibilityOptionsHelper(this);

    public OptionsHelper(GameOptions options) {
        super(options);
    }

    /**
     * @return a helper for the skin options.
     * @since 1.8.4
     */
    public SkinOptionsHelper getSkinOptions() {
        return skin;
    }

    /**
     * @return a helper for the video options.
     * @since 1.8.4
     */
    public VideoOptionsHelper getVideoOptions() {
        return video;
    }

    /**
     * @return a helper for the music options.
     * @since 1.8.4
     */
    public MusicOptionsHelper getMusicOptions() {
        return music;
    }

    /**
     * @return a helper for the control options.
     * @since 1.8.4
     */
    public ControlOptionsHelper getControlOptions() {
        return control;
    }

    /**
     * @return a helper for the chat options.
     * @since 1.8.4
     */
    public ChatOptionsHelper getChatOptions() {
        return chat;
    }

    /**
     * @return a helper for the accessibility options.
     * @since 1.8.4
     */
    public AccessibilityOptionsHelper getAccessibilityOptions() {
        return accessibility;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public OptionsHelper saveOptions() {
        base.write();
        return this;
    }

    /**
     * @return list of names of resource packs.
     * @since 1.1.7
     */
    public List<String> getResourcePacks() {
        return new ArrayList<>(rpm.getNames());
    }

    /**
     * @return list of names of enabled resource packs.
     * @since 1.2.0
     */
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<>(rpm.getEnabledNames());
    }

    /**
     * Set the enabled resource packs to the provided list.
     *
     * @param enabled
     * @return self for chaining.
     * @since 1.2.0
     */
    public OptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = Arrays.stream(enabled).distinct().collect(Collectors.toList());
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
     * @param state false to put it back
     * @since 1.8.3
     */
    public OptionsHelper removeServerResourcePack(boolean state) {
        if (state != ((IResourcePackManager) rpm).jsmacros_isServerPacksDisabled()) {
            ((IResourcePackManager) rpm).jsmacros_disableServerPacks(state);
            mc.reloadResources();
        }
        return this;
    }

    /**
     * @return the active language.
     * @since 1.8.4
     */
    @DocletReplaceReturn("Locale")
    public String getLanguage() {
        return base.language;
    }

    /**
     * @param languageCode the language to change to
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("languageCode: Locale")
    public OptionsHelper setLanguage(String languageCode) {
        LanguageManager manager = MinecraftClient.getInstance().getLanguageManager();
        LanguageDefinition language = manager.getLanguage(languageCode);
        if (language != null) {
            manager.setLanguage(languageCode);
            base.language = languageCode;
            base.write();
            mc.reloadResources();
        }
        MinecraftClient.getInstance().reloadResources();
        base.write();
        return this;
    }

    /**
     * @return the active difficulty.
     * @since 1.8.4
     */
    @DocletReplaceReturn("Difficulty")
    public String getDifficulty() {
        return mc.world.getDifficulty().getName();
    }

    /**
     * The name be either "peaceful", "easy", "normal", or "hard".
     *
     * @param name the name of the difficulty to change to
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("name: Difficulty")
    @DocletEnumType(name = "Difficulty", type = "'peaceful' | 'easy' | 'normal' | 'hard'")
    public OptionsHelper setDifficulty(String name) {
        if (mc.isIntegratedServerRunning()) {
            mc.getServer().setDifficulty(Difficulty.byName(name), true);
        }
        return this;
    }

    /**
     * @return {@code true} if the difficulty is locked, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDifficultyLocked() {
        return MinecraftClient.getInstance().world.getLevelProperties().isDifficultyLocked();
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public OptionsHelper lockDifficulty() {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
        return this;
    }

    /**
     * Unlocks the difficulty of the world. This can't be done in an unmodified client.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public OptionsHelper unlockDifficulty() {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(false));
        return this;
    }

    /**
     * @return the current fov value.
     * @since 1.1.7
     */
    public int getFov() {
        return base.getFov().getValue();
    }

    /**
     * @param fov the new fov value
     * @return self for chaining.
     * @since 1.1.7
     */
    public OptionsHelper setFov(int fov) {
        getBase(base.getFov()).forceSetValue(fov);
        return this;
    }

    /**
     * @return 0 for 1st person, 2 for in front.
     * @since 1.5.0
     */
    @DocletReplaceReturn("Trit")
    public int getCameraMode() {
        return base.getPerspective().ordinal();
    }

    /**
     * @param mode 0: first, 2: front
     * @since 1.5.0
     */
    @DocletReplaceParams("mode: Trit")
    public OptionsHelper setCameraMode(int mode) {
        base.setPerspective(Perspective.values()[mode]);
        return this;
    }

    /**
     * @return
     * @since 1.5.0
     */
    public boolean getSmoothCamera() {
        return base.smoothCameraEnabled;
    }

    /**
     * @param val
     * @since 1.5.0
     */
    public OptionsHelper setSmoothCamera(boolean val) {
        base.smoothCameraEnabled = val;
        return this;
    }

    /**
     * @return
     * @since 1.2.6
     */
    public int getWidth() {
        return mc.getWindow().getWidth();
    }

    /**
     * @return
     * @since 1.2.6
     */
    public int getHeight() {
        return mc.getWindow().getHeight();
    }

    /**
     * @param w
     * @since 1.2.6
     */
    public OptionsHelper setWidth(int w) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, win.getHeight());
        return this;
    }

    /**
     * @param h
     * @since 1.2.6
     */
    public OptionsHelper setHeight(int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), win.getWidth(), h);
        return this;
    }

    /**
     * @param w
     * @param h
     * @since 1.2.6
     */
    public OptionsHelper setSize(int w, int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, h);
        return this;
    }

    private MixinSimpleOption getBase(SimpleOption<?> option) {
        return (MixinSimpleOption) (Object) option;
    }

    public class SkinOptionsHelper {

        public final OptionsHelper parent;

        public SkinOptionsHelper(OptionsHelper OptionsHelper) {
            parent = OptionsHelper;
        }

        /**
         * @return the parent options helper.
         * @since 1.8.4
         */
        public OptionsHelper getParent() {
            return parent;
        }

        /**
         * @return {@code true} if the player's cape should be shown, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isCapeActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.CAPE);
        }

        /**
         * @return {@code true} if the player's jacket should be shown, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isJacketActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.JACKET);
        }

        /**
         * @return {@code true} if the player's left sleeve should be shown, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isLeftSleeveActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE);
        }

        /**
         * @return {@code true} if the player's right sleeve should be shown, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isRightSleeveActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE);
        }

        /**
         * @return {@code true} if the player's left pants should be shown, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isLeftPantsActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG);
        }

        /**
         * @return {@code true} if the player's right pants should be shown, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isRightPantsActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG);
        }

        /**
         * @return {@code true} if the player's hat should be shown, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isHatActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.HAT);
        }

        /**
         * @return {@code true} if the player's main hand is the right one, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isRightHanded() {
            return base.getMainArm().getValue() == Arm.RIGHT;
        }

        /**
         * @return {@code true} if the player's main hand is the left one, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isLeftHanded() {
            return base.getMainArm().getValue() == Arm.LEFT;
        }

        /**
         * @param val whether the cape should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleCape(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.CAPE, val);
            return this;
        }

        /**
         * @param val whether the jacket should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleJacket(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.JACKET, val);
            return this;
        }

        /**
         * @param val whether the left sleeve should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleLeftSleeve(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.LEFT_SLEEVE, val);
            return this;
        }

        /**
         * @param val whether the right sleeve should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleRightSleeve(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, val);
            return this;
        }

        /**
         * @param val whether the left pants should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleLeftPants(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, val);
            return this;
        }

        /**
         * @param val whether the right pants should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleRightPants(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, val);
            return this;
        }

        /**
         * @param val whether the hat should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleHat(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.HAT, val);
            return this;
        }

        /**
         * The hand must be either {@code "left"} or {@code "right"}.
         *
         * @param hand the hand to set as main hand
         * @return self for chaining.
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleMainHand(String hand) {
            base.getMainArm().setValue(hand.toLowerCase(Locale.ROOT).equals("left") ? Arm.LEFT : Arm.RIGHT);
            return this;
        }

    }

    public class VideoOptionsHelper {

        public final OptionsHelper parent;

        public VideoOptionsHelper(OptionsHelper OptionsHelper) {
            parent = OptionsHelper;
        }

        /**
         * @return the parent options helper.
         * @since 1.8.4
         */
        public OptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the full screen resolution as a string.
         * @since 1.8.4
         */
        public String getFullscreenResolution() {
            return base.fullscreenResolution;
        }

        /**
         * @return the current biome blend radius.
         * @since 1.8.4
         */
        public int getBiomeBlendRadius() {
            return base.getBiomeBlendRadius().getValue();
        }

        /**
         * @param radius the new biome blend radius
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setBiomeBlendRadius(int radius) {
            getBase(base.getBiomeBlendRadius()).forceSetValue(radius);
            return this;
        }

        /**
         * @return the selected graphics mode.
         * @since 1.8.4
         */
        @DocletReplaceReturn("GraphicsMode")
        public String getGraphicsMode() {
            switch (base.getGraphicsMode().getValue()) {
                case FAST:
                    return "fast";
                case FANCY:
                    return "fancy";
                case FABULOUS:
                    return "fabulous";
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * @param mode the graphics mode to select. Must be either "fast", "fancy" or "fabulous"
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("mode: GraphicsMode")
        @DocletEnumType(name = "GraphicsMode", type = "'fast' | 'fancy' | 'fabulous'")
        public VideoOptionsHelper setGraphicsMode(String mode) {
            GraphicsMode newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "FAST":
                    newMode = GraphicsMode.FAST;
                    break;
                case "FANCY":
                    newMode = GraphicsMode.FANCY;
                    break;
                case "FABULOUS":
                    newMode = GraphicsMode.FABULOUS;
                    break;
                default:
                    newMode = base.getGraphicsMode().getValue();
                    break;
            }
            base.getGraphicsMode().setValue(newMode);
            return this;
        }

        /**
         * @return the selected chunk builder mode.
         * @since 1.8.4
         */
        @DocletReplaceReturn("ChunkBuilderMode")
        public String getChunkBuilderMode() {
            switch (base.getChunkBuilderMode().getValue()) {
                case NONE:
                    return "none";
                case NEARBY:
                    return "nearby";
                case PLAYER_AFFECTED:
                    return "player_affected";
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * @param mode the chunk builder mode to select. Must be either "none", "nearby" or
         *             "player_affected"
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("mode: ChunkBuilderMode")
        @DocletEnumType(name = "ChunkBuilderMode", type = "'none' | 'nearby' | 'player_affected'")
        public VideoOptionsHelper setChunkBuilderMode(String mode) {
            ChunkBuilderMode newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "NONE":
                    newMode = ChunkBuilderMode.NONE;
                    break;
                case "NEARBY":
                    newMode = ChunkBuilderMode.NEARBY;
                    break;
                case "PLAYER_AFFECTED":
                    newMode = ChunkBuilderMode.PLAYER_AFFECTED;
                    break;
                default:
                    newMode = base.getChunkBuilderMode().getValue();
                    break;
            }
            base.getChunkBuilderMode().setValue(newMode);
            return this;
        }

        /**
         * @return the selected smooth lightning mode.
         * @since 1.8.4
         */
        public boolean getSmoothLightningMode() {
            return base.getAo().getValue();
        }

        /**
         * @param mode the smooth lightning mode to select. boolean value
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setSmoothLightningMode(boolean mode) {
            base.getAo().setValue(mode);
            return this;
        }

        /**
         * @return the current render distance in chunks.
         * @since 1.8.4
         */
        public int getRenderDistance() {
            return base.getViewDistance().getValue();
        }

        /**
         * @param radius the new render distance in chunks
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setRenderDistance(int radius) {
            base.getViewDistance().setValue(radius);
            return this;
        }

        /**
         * @return the current simulation distance in chunks.
         * @since 1.8.4
         */
        public int getSimulationDistance() {
            return base.getSimulationDistance().getValue();
        }

        /**
         * @param radius the new simulation distance in chunks
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setSimulationDistance(int radius) {
            base.getSimulationDistance().setValue(radius);
            return this;
        }

        /**
         * @return the current upper fps limit.
         * @since 1.8.4
         */
        public int getMaxFps() {
            return base.getMaxFps().getValue();
        }

        /**
         * @param maxFps the new maximum fps limit
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setMaxFps(int maxFps) {
            base.getMaxFps().setValue(maxFps);
            return this;
        }

        /**
         * @return {@code true} if vsync is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isVsyncEnabled() {
            return base.getEnableVsync().getValue();
        }

        /**
         * @param val whether to enable vsync or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper enableVsync(boolean val) {
            base.getEnableVsync().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if view bobbing is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isViewBobbingEnabled() {
            return base.getBobView().getValue();
        }

        /**
         * @param val whether to enable view bobbing or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper enableViewBobbing(boolean val) {
            base.getBobView().setValue(val);
            return this;
        }

        /**
         * @return the current gui scale.
         * @since 1.8.4
         */
        public int getGuiScale() {
            return base.getGuiScale().getValue();
        }

        /**
         * @param scale the gui scale to set. Must be 1, 2, 3 or 4
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setGuiScale(int scale) {
            base.getGuiScale().setValue(scale);
            mc.execute(mc::onResolutionChanged);
            return this;
        }

        /**
         * @return the current attack indicator type.
         * @since 1.8.4
         */
        @DocletReplaceReturn("AttackIndicatorType")
        public String getAttackIndicatorType() {
            switch (base.getAttackIndicator().getValue()) {
                case OFF:
                    return "off";
                case CROSSHAIR:
                    return "crosshair";
                case HOTBAR:
                    return "hotbar";
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * @param type the attack indicator type. Must be either "off", "crosshair", or "hotbar"
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("type: AttackIndicatorType")
        @DocletEnumType(name = "AttackIndicatorType", type = "'off' | 'crosshair' | 'hotbar'")
        public VideoOptionsHelper setAttackIndicatorType(String type) {
            AttackIndicator newType;
            switch (type.toUpperCase(Locale.ROOT)) {
                case "OFF":
                    newType = AttackIndicator.OFF;
                    break;
                case "CROSSHAIR":
                    newType = AttackIndicator.CROSSHAIR;
                    break;
                case "HOTBAR":
                    newType = AttackIndicator.HOTBAR;
                    break;
                default:
                    newType = base.getAttackIndicator().getValue();
                    break;
            }
            base.getAttackIndicator().setValue(newType);
            return this;
        }

        /**
         * @return the current gamma value.
         * @since 1.8.4
         */
        public double getGamma() {
            return getBrightness();
        }

        /**
         * @param gamma the new gamma value
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setGamma(double gamma) {
            return setBrightness(gamma);
        }

        /**
         * @return the current brightness value.
         * @since 1.8.4
         */
        public double getBrightness() {
            return base.getGamma().getValue();
        }

        /**
         * @param gamma the new brightness value
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setBrightness(double gamma) {
            getBase(base.getGamma()).forceSetValue(gamma);
            return this;
        }

        /**
         * @return the current cloud rendering mode.
         * @since 1.8.4
         */
        @DocletReplaceReturn("CloudsMode")
        public String getCloudsMode() {
            switch (base.getCloudRenderMode().getValue()) {
                case OFF:
                    return "off";
                case FAST:
                    return "fast";
                case FANCY:
                    return "fancy";
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * @param mode the cloud rendering mode to select. Must be either "off", "fast" or "fancy"
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("mode: CloudsMode")
        @DocletEnumType(name = "CloudsMode", type = "'off' | 'fast' | 'fancy'")
        public VideoOptionsHelper setCloudsMode(String mode) {
            CloudRenderMode newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF":
                    newMode = CloudRenderMode.OFF;
                    break;
                case "FAST":
                    newMode = CloudRenderMode.FAST;
                    break;
                case "FANCY":
                    newMode = CloudRenderMode.FANCY;
                    break;
                default:
                    newMode = base.getCloudRenderMode().getValue();
                    break;
            }
            base.getCloudRenderMode().setValue(newMode);
            return this;
        }

        /**
         * @return {@code true} if the game is running in fullscreen mode, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isFullscreen() {
            return base.getFullscreen().getValue();
        }

        /**
         * @param fullscreen whether to enable fullscreen mode or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setFullScreen(boolean fullscreen) {
            base.getFullscreen().setValue(fullscreen);
            return this;
        }

        /**
         * @return the current particle rendering mode.
         * @since 1.8.4
         */
        @DocletReplaceReturn("ParticleMode")
        public String getParticleMode() {
            switch (base.getParticles().getValue()) {
                case MINIMAL:
                    return "minimal";
                case DECREASED:
                    return "decreased";
                case ALL:
                    return "all";
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * @param mode the particle rendering mode to select. Must be either "minimal", "decreased"
         *             or "all"
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("mode: ParticleMode")
        @DocletEnumType(name = "ParticleMode", type = "'minimal' | 'decreased' | 'all'")
        public VideoOptionsHelper setParticleMode(String mode) {
            ParticlesMode newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "MINIMAL":
                    newMode = ParticlesMode.MINIMAL;
                    break;
                case "DECREASED":
                    newMode = ParticlesMode.DECREASED;
                    break;
                case "ALL":
                    newMode = ParticlesMode.ALL;
                    break;
                default:
                    newMode = base.getParticles().getValue();
                    break;
            }
            base.getParticles().setValue(newMode);
            return this;
        }

        /**
         * @return the current mip map level.
         * @since 1.8.4
         */
        public int getMipMapLevels() {
            return base.getMipmapLevels().getValue();
        }

        /**
         * @param val the new mip map level
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setMipMapLevels(int val) {
            base.getMipmapLevels().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if entity shadows should be rendered, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean areEntityShadowsEnabled() {
            return base.getEntityShadows().getValue();
        }

        /**
         * @param val whether to enable entity shadows or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper enableEntityShadows(boolean val) {
            base.getEntityShadows().setValue(val);
            return this;
        }

        /**
         * @return the current distortion effect scale.
         * @since 1.8.4
         */
        public double getDistortionEffect() {
            return base.getDistortionEffectScale().getValue();
        }

        /**
         * @param val the new distortion effect scale
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setDistortionEffects(double val) {
            base.getDistortionEffectScale().setValue(val);
            return this;
        }

        /**
         * @return the current entity render distance.
         * @since 1.8.4
         */
        public double getEntityDistance() {
            return base.getEntityDistanceScaling().getValue();
        }

        /**
         * @param val the new entity render distance
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setEntityDistance(double val) {
            base.getEntityDistanceScaling().setValue(val);
            return this;
        }

        /**
         * @return the current fov value.
         * @since 1.8.4
         */
        public double getFovEffects() {
            return base.getFovEffectScale().getValue();
        }

        /**
         * @param val the new fov value
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper setFovEffects(double val) {
            getBase(base.getFovEffectScale()).forceSetValue(val);
            return this;
        }

        /**
         * @return {@code true} if the autosave indicator is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isAutosaveIndicatorEnabled() {
            return base.getShowAutosaveIndicator().getValue();
        }

        /**
         * @return self for chaining.
         * @since 1.8.4
         */
        public VideoOptionsHelper enableAutosaveIndicator(boolean val) {
            base.getShowAutosaveIndicator().setValue(val);
            return this;
        }

    }

    public class MusicOptionsHelper {

        public final OptionsHelper parent;

        public MusicOptionsHelper(OptionsHelper OptionsHelper) {
            parent = OptionsHelper;
        }

        /**
         * @return the parent options helper.
         * @since 1.8.4
         */
        public OptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current master volume.
         * @since 1.8.4
         */
        public float getMasterVolume() {
            return base.getSoundVolume(SoundCategory.MASTER);
        }

        /**
         * @param volume the new master volume
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setMasterVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.MASTER).setValue(volume);
            return this;
        }

        /**
         * @return the current music volume.
         * @since 1.8.4
         */
        public float getMusicVolume() {
            return base.getSoundVolume(SoundCategory.MUSIC);
        }

        /**
         * @param volume the new music volume
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setMusicVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.MUSIC).setValue(volume);
            return this;
        }

        /**
         * @return the current value of played recods.
         * @since 1.8.4
         */
        public float getRecordsVolume() {
            return base.getSoundVolume(SoundCategory.RECORDS);
        }

        /**
         * @param volume the new volume for playing records
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setRecordsVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.RECORDS).setValue(volume);
            return this;
        }

        /**
         * @return the current volume of the weather.
         * @since 1.8.4
         */
        public float getWeatherVolume() {
            return base.getSoundVolume(SoundCategory.WEATHER);
        }

        /**
         * @param volume the new volume for the weather
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setWeatherVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.WEATHER).setValue(volume);
            return this;
        }

        /**
         * @return the current volume of block related sounds.
         * @since 1.8.4
         */
        public float getBlocksVolume() {
            return base.getSoundVolume(SoundCategory.BLOCKS);
        }

        /**
         * @param volume the new volume for block sounds
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setBlocksVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.BLOCKS).setValue(volume);
            return this;
        }

        /**
         * @return the current volume of hostile mobs.
         * @since 1.8.4
         */
        public float getHostileVolume() {
            return base.getSoundVolume(SoundCategory.HOSTILE);
        }

        /**
         * @param volume the new volume for hostile mobs
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setHostileVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.HOSTILE).setValue(volume);
            return this;
        }

        /**
         * @return the current volume of neutral mobs.
         * @since 1.8.4
         */
        public float getNeutralVolume() {
            return base.getSoundVolume(SoundCategory.NEUTRAL);
        }

        /**
         * @param volume the new volume for neutral mobs
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setNeutralVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.NEUTRAL).setValue(volume);
            return this;
        }

        /**
         * @return the current player volume.
         * @since 1.8.4
         */
        public float getPlayerVolume() {
            return base.getSoundVolume(SoundCategory.PLAYERS);
        }

        /**
         * @param volume the new player volume
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setPlayerVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.PLAYERS).setValue(volume);
            return this;
        }

        /**
         * @return the current ambient volume.
         * @since 1.8.4
         */
        public float getAmbientVolume() {
            return base.getSoundVolume(SoundCategory.AMBIENT);
        }

        /**
         * @param volume the new ambient volume
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setAmbientVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.AMBIENT).setValue(volume);
            return this;
        }

        /**
         * @return the current voice volume.
         * @since 1.8.4
         */
        public float getVoiceVolume() {
            return base.getSoundVolume(SoundCategory.VOICE);
        }

        /**
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setVoiceVolume(double volume) {
            base.getSoundVolumeOption(SoundCategory.VOICE).setValue(volume);
            return this;
        }

        /**
         * @param category the category to get the volume of
         * @return the volume of the given sound category.
         * @since 1.8.4
         */
        @DocletReplaceParams("category: SoundCategory")
        public float getVolume(String category) {
            return base.getSoundVolume(SOUND_CATEGORY_MAP.get(category));
        }

        /**
         * @return a map of all sound categories and their volumes.
         * @since 1.8.4
         */
        public Map<String, Float> getVolumes() {
            Map<String, Float> volumes = new HashMap<>();
            for (SoundCategory category : SoundCategory.values()) {
                volumes.put(category.getName(), base.getSoundVolume(category));
            }
            return volumes;
        }

        /**
         * @param category the category to set the volume for
         * @param volume   the new volume
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("category: SoundCategory, volume: double")
        public MusicOptionsHelper setVolume(String category, double volume) {
            base.getSoundVolumeOption(SOUND_CATEGORY_MAP.get(category)).setValue(volume);
            return this;
        }

        /**
         * @return the currently selected sound device.
         * @since 1.8.4
         */
        public String getSoundDevice() {
            return base.getSoundDevice().getValue();
        }

        /**
         * @param audioDevice the audio device to use
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper setSoundDevice(String audioDevice) {
            List<String> audioDevices = getAudioDevices();
            if (!audioDevices.contains(audioDevice)) {
                audioDevice = "";
            }
            base.getSoundDevice().setValue(audioDevice);
            SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
            soundManager.reloadSounds();
            return this;
        }

        /**
         * @return a list of all connected audio devices.
         * @since 1.8.4
         */
        public List<String> getAudioDevices() {
            return Stream.concat(Stream.of(""), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).collect(Collectors.toList());
        }

        /**
         * @return {@code true} if subtitles should be shown, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean areSubtitlesShown() {
            return base.getShowSubtitles().getValue();
        }

        /**
         * @param val whether subtitles should be shown or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public MusicOptionsHelper showSubtitles(boolean val) {
            base.getShowSubtitles().setValue(val);
            return this;
        }

    }

    public class ControlOptionsHelper {

        public final OptionsHelper parent;

        public ControlOptionsHelper(OptionsHelper OptionsHelper) {
            parent = OptionsHelper;
        }

        /**
         * @return the parent options helper.
         * @since 1.8.4
         */
        public OptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current mouse sensitivity.
         * @since 1.8.4
         */
        public double getMouseSensitivity() {
            return base.getMouseSensitivity().getValue();
        }

        /**
         * @param val the new mouse sensitivity
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper setMouseSensitivity(double val) {
            getBase(base.getMouseSensitivity()).forceSetValue(val);
            return this;
        }

        /**
         * @return {@code true} if the mouse direction should be inverted.
         * @since 1.8.4
         */
        public boolean isMouseInverted() {
            return base.getInvertYMouse().getValue();
        }

        /**
         * @param val whether to invert the mouse direction or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper invertMouse(boolean val) {
            base.getInvertYMouse().setValue(val);
            return this;
        }

        /**
         * @return the current mouse wheel sensitivity.
         * @since 1.8.4
         */
        public double getMouseWheelSensitivity() {
            return base.getMouseWheelSensitivity().getValue();
        }

        /**
         * @param val the new mouse wheel sensitivity
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper setMouseWheelSensitivity(double val) {
            getBase(base.getMouseWheelSensitivity()).forceSetValue(val);
            return this;
        }

        /**
         * This option was introduced due to a bug on some systems where the mouse wheel would
         * scroll too fast.
         *
         * @return {@code true} if discrete scrolling is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isDiscreteScrollingEnabled() {
            return base.getDiscreteMouseScroll().getValue();
        }

        /**
         * @param val whether to enable discrete scrolling or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper enableDiscreteScrolling(boolean val) {
            base.getDiscreteMouseScroll().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if touchscreen mode is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isTouchscreenEnabled() {
            return base.getTouchscreen().getValue();
        }

        /**
         * @param val whether to enable touchscreen mode or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper enableTouchscreen(boolean val) {
            base.getTouchscreen().setValue(val);
            return this;
        }

        /**
         * Raw input is directly reading the mouse data, without any adjustments due to other
         * programs or the operating system.
         *
         * @return {@code true} if raw mouse input is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isRawMouseInputEnabled() {
            return base.getRawMouseInput().getValue();
        }

        /**
         * @param val whether to enable raw mouse input or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper enableRawMouseInput(boolean val) {
            base.getRawMouseInput().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if auto jump is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isAutoJumpEnabled() {
            return base.getAutoJump().getValue();
        }

        /**
         * @param val whether to enable auto jump or not or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper enableAutoJump(boolean val) {
            base.getAutoJump().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sneaking is enabled, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isSneakTogglingEnabled() {
            return base.getSneakToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sneaking
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper toggleSneak(boolean val) {
            base.getSneakToggled().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sprinting is enabled, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isSprintTogglingEnabled() {
            return base.getSprintToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sprinting
         * @return self for chaining.
         * @since 1.8.4
         */
        public ControlOptionsHelper toggleSprint(boolean val) {
            base.getSprintToggled().setValue(val);
            return this;
        }

        /**
         * @return an array of all raw minecraft keybindings.
         * @since 1.8.4
         */
        public KeyBinding[] getRawKeys() {
            return ArrayUtils.clone(base.allKeys);
        }

        /**
         * @return a list of all keybinding catehories.
         * @since 1.8.4
         */
        @DocletReplaceReturn("JavaList<KeyCategory>")
        public List<String> getCategories() {
            return Arrays.stream(base.allKeys).map(KeyBinding::getCategory).distinct().collect(Collectors.toList());
        }

        /**
         * @return a list of all key names.
         * @since 1.8.4
         */
        @DocletReplaceReturn("JavaList<Key>")
        public List<String> getKeys() {
            return Arrays.stream(base.allKeys).map(KeyBinding::getTranslationKey).collect(Collectors.toList());
        }

        /**
         * @return a map of all keybindings and their bound key.
         * @since 1.8.4
         */
        @DocletReplaceReturn("JavaMap<Bind, Key>")
        public Map<String, String> getKeyBinds() {
            Map<String, String> keyBinds = new HashMap<>(base.allKeys.length);

            for (KeyBinding key : base.allKeys) {
                keyBinds.put(Text.translatable(key.getTranslationKey()).getString(), key.getBoundKeyLocalizedText().getString());
            }
            return keyBinds;
        }

        /**
         * @param category the category to get keybindings from
         * @return a map of all keybindings and their bound key in the specified category.
         * @since 1.8.4
         */
        @DocletReplaceParams("category: KeyCategory")
        public Map<String, String> getKeyBindsByCategory(String category) {
            return getKeyBindsByCategory().get(category);
        }

        /**
         * @return a map of all keybinding categories, containing a map of all keybindings in that
         * category and their bound key.
         * @since 1.8.4
         */
        public Map<String, Map<String, String>> getKeyBindsByCategory() {
            Map<String, Map<String, String>> entries = new HashMap<>(MinecraftClient.getInstance().options.allKeys.length);

            for (KeyBinding key : MinecraftClient.getInstance().options.allKeys) {
                Map<String, String> categoryMap;
                String category = key.getCategory();
                if (!entries.containsKey(category)) {
                    categoryMap = new HashMap<>();
                    entries.put(category, categoryMap);
                } else {
                    categoryMap = entries.get(category);
                }
                categoryMap.put(Text.translatable(key.getTranslationKey()).getString(), key.getBoundKeyLocalizedText().getString());
            }
            return entries;
        }

    }

    public class ChatOptionsHelper {

        public final OptionsHelper parent;

        public ChatOptionsHelper(OptionsHelper OptionsHelper) {
            parent = OptionsHelper;
        }

        /**
         * @return the parent options helper.
         * @since 1.8.4
         */
        public OptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current chat visibility mode.
         * @since 1.8.4
         */
        @DocletReplaceReturn("ChatVisibility")
        public String getChatVisibility() {
            String chatVisibilityKey = base.getChatVisibility().getValue().getTranslationKey();
            return chatVisibilityKey.substring(chatVisibilityKey.lastIndexOf('.'));
        }

        /**
         * @param mode the new chat visibility mode. Must be "FULL", "SYSTEM" or "HIDDEN
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("mode: ChatVisibility")
        @DocletEnumType(name = "ChatVisibility", type = "'FULL' | 'SYSTEM' | 'HIDDEN'")
        public ChatOptionsHelper setChatVisibility(String mode) {
            ChatVisibility newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "FULL":
                    newMode = ChatVisibility.FULL;
                    break;
                case "SYSTEM":
                    newMode = ChatVisibility.SYSTEM;
                    break;
                case "HIDDEN":
                    newMode = ChatVisibility.HIDDEN;
                    break;
                default:
                    newMode = base.getChatVisibility().getValue();
                    break;
            }
            base.getChatVisibility().setValue(newMode);
            return this;
        }

        /**
         * @return {@code true} if messages can use color codes, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean areColorsShown() {
            return base.getChatColors().getValue();
        }

        /**
         * @param val whether to allow color codes in messages or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setShowColors(boolean val) {
            base.getChatColors().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if it's allowed to open web links from chat, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean areWebLinksEnabled() {
            return base.getChatLinks().getValue();
        }

        /**
         * @param val whether to allow opening web links from chat or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper enableWebLinks(boolean val) {
            base.getChatLinks().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if a warning prompt before opening links should be shown,
         * {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isWebLinkPromptEnabled() {
            return base.getChatLinksPrompt().getValue();
        }

        /**
         * @param val whether to show warning prompts before opening links or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper enableWebLinkPrompt(boolean val) {
            base.getChatLinksPrompt().setValue(val);
            return this;
        }

        /**
         * @return the current chat opacity.
         * @since 1.8.4
         */
        public double getChatOpacity() {
            return base.getChatOpacity().getValue();
        }

        /**
         * @param val the new chat opacity
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatOpacity(double val) {
            base.getChatOpacity().setValue(val);
            return this;
        }

        /**
         * @param val the new background opacity for text
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setTextBackgroundOpacity(double val) {
            getBase(base.getTextBackgroundOpacity()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current background opacity of text.
         * @since 1.8.4
         */
        public double getTextBackgroundOpacity() {
            return base.getTextBackgroundOpacity().getValue();
        }

        /**
         * @return the current text size.
         * @since 1.8.4
         */
        public double getTextSize() {
            return base.getChatScale().getValue();
        }

        /**
         * @param val the new text size
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setTextSize(double val) {
            getBase(base.getChatScale()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current chat line spacing.
         * @since 1.8.4
         */
        public double getChatLineSpacing() {
            return base.getChatLineSpacing().getValue();
        }

        /**
         * @param val the new chat line spacing
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatLineSpacing(double val) {
            getBase(base.getChatLineSpacing()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current chat delay in seconds.
         * @since 1.8.4
         */
        public double getChatDelay() {
            return base.getChatDelay().getValue();
        }

        /**
         * @param val the new chat delay in seconds
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatDelay(double val) {
            base.getChatDelay().setValue(val);
            return this;
        }

        /**
         * @return the current chat width.
         * @since 1.8.4
         */
        public double getChatWidth() {
            return base.getChatWidth().getValue();
        }

        /**
         * @param val the new chat width
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatWidth(double val) {
            base.getChatWidth().setValue(val);
            return this;
        }

        /**
         * @return the focused chat height.
         * @since 1.8.4
         */
        public double getChatFocusedHeight() {
            return base.getChatHeightFocused().getValue();
        }

        /**
         * @param val the new focused chat height
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatFocusedHeight(double val) {
            base.getChatHeightFocused().setValue(val);
            return this;
        }

        /**
         * @return the unfocused chat height.
         * @since 1.8.4
         */
        public double getChatUnfocusedHeight() {
            return base.getChatHeightUnfocused().getValue();
        }

        /**
         * @param val the new unfocused chat height
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatUnfocusedHeight(double val) {
            getBase(base.getChatHeightUnfocused()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current narrator mode.
         * @since 1.8.4
         */
        @DocletReplaceReturn("NarratorMode")
        public String getNarratorMode() {
            String narratorKey = ((TranslatableTextContent) (base.getNarrator().getValue().getName().getContent())).getKey();
            return narratorKey.substring(narratorKey.lastIndexOf('.'));
        }

        /**
         * @param mode the mode to set the narrator to. Must be either "OFF", "ALL", "CHAT", or
         *             "SYSTEM"
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("mode: NarratorMode")
        @DocletEnumType(name = "NarratorMode", type = "'OFF' | 'ALL' | 'CHAT' | 'SYSTEM'")
        public ChatOptionsHelper setNarratorMode(String mode) {
            NarratorMode newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF":
                    newMode = NarratorMode.OFF;
                    break;
                case "ALL":
                    newMode = NarratorMode.ALL;
                    break;
                case "CHAT":
                    newMode = NarratorMode.CHAT;
                    break;
                case "SYSTEM":
                    newMode = NarratorMode.SYSTEM;
                    break;
                default:
                    newMode = base.getNarrator().getValue();
                    break;
            }
            base.getNarrator().setValue(newMode);
            return this;
        }

        /**
         * @return {@code true} if command suggestions are enabled
         * @since 1.8.4
         */
        public boolean areCommandSuggestionsEnabled() {
            return base.getAutoSuggestions().getValue();
        }

        /**
         * @param val whether to enable command suggestions or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper enableCommandSuggestions(boolean val) {
            base.getAutoSuggestions().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if messages from blocked users are hidden.
         * @since 1.8.4
         */
        public boolean areMatchedNamesHidden() {
            return base.getHideMatchedNames().getValue();
        }

        /**
         * @param val whether to hide messages of blocked users or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper enableHideMatchedNames(boolean val) {
            base.getHideMatchedNames().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if reduced debug info is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isDebugInfoReduced() {
            return base.getReducedDebugInfo().getValue();
        }

        /**
         * @param val whether to enable reduced debug info or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public ChatOptionsHelper reduceDebugInfo(boolean val) {
            base.getReducedDebugInfo().setValue(val);
            return this;
        }

    }

    public class AccessibilityOptionsHelper {

        public final OptionsHelper parent;

        public AccessibilityOptionsHelper(OptionsHelper OptionsHelper) {
            parent = OptionsHelper;
        }

        /**
         * @return the parent options helper.
         * @since 1.8.4
         */
        public OptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current narrator mode.
         * @since 1.8.4
         */
        public String getNarratorMode() {
            String narratorKey = ((TranslatableTextContent) (base.getNarrator().getValue().getName().getContent())).getKey();
            return narratorKey.substring(narratorKey.lastIndexOf('.'));
        }

        /**
         * @param mode the mode to set the narrator to. Must be either "OFF", "ALL", "CHAT", or
         *             "SYSTEM"
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setNarratorMode(String mode) {
            NarratorMode newMode;
            switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF":
                    newMode = NarratorMode.OFF;
                    break;
                case "ALL":
                    newMode = NarratorMode.ALL;
                    break;
                case "CHAT":
                    newMode = NarratorMode.CHAT;
                    break;
                case "SYSTEM":
                    newMode = NarratorMode.SYSTEM;
                    break;
                default:
                    newMode = base.getNarrator().getValue();
                    break;
            }
            base.getNarrator().setValue(newMode);
            return this;
        }

        /**
         * @return {@code true} if subtitles are enabled.
         * @since 1.8.4
         */
        public boolean areSubtitlesShown() {
            return base.getShowSubtitles().getValue();
        }

        /**
         * @param val whether to show subtitles or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper showSubtitles(boolean val) {
            base.getShowSubtitles().setValue(val);
            return this;
        }

        /**
         * @param val the new opacity for the text background
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setTextBackgroundOpacity(double val) {
            base.getTextBackgroundOpacity().setValue(val);
            return this;
        }

        /**
         * @return the opacity of the text background.
         * @since 1.8.4
         */
        public double getTextBackgroundOpacity() {
            return base.getTextBackgroundOpacity().getValue();
        }

        /**
         * @return
         * @since 1.8.4
         */
        public boolean isBackgroundForChatOnly() {
            return base.getBackgroundForChatOnly().getValue();
        }

        /**
         * @param val
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableBackgroundForChatOnly(boolean val) {
            base.getBackgroundForChatOnly().setValue(val);
            return this;
        }

        /**
         * @return the current chat opacity.
         * @since 1.8.4
         */
        public double getChatOpacity() {
            return base.getChatOpacity().getValue();
        }

        /**
         * @param val the new chat opacity
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatOpacity(double val) {
            base.getChatOpacity().setValue(val);
            return this;
        }

        /**
         * @return the current chat line spacing.
         * @since 1.8.4
         */
        public double getChatLineSpacing() {
            return base.getChatLineSpacing().getValue();
        }

        /**
         * @param val the new chat line spacing
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatLineSpacing(double val) {
            getBase(base.getChatLineSpacing()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current chat delay in seconds.
         * @since 1.8.4
         */
        public double getChatDelay() {
            return base.getChatDelay().getValue();
        }

        /**
         * @param val the new chat delay in seconds
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatDelay(double val) {
            getBase(base.getChatDelay()).forceSetValue(val);
            return this;
        }

        /**
         * @return {@code true} if auto jump is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isAutoJumpEnabled() {
            return base.getAutoJump().getValue();
        }

        /**
         * @param val whether to enable auto jump or not or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableAutoJump(boolean val) {
            base.getAutoJump().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sneaking is enabled, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isSneakTogglingEnabled() {
            return base.getSneakToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sneaking
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper toggleSneak(boolean val) {
            base.getSneakToggled().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sprinting is enabled, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isSprintTogglingEnabled() {
            return base.getSprintToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sprinting
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper toggleSprint(boolean val) {
            base.getSprintToggled().setValue(val);
            return this;
        }

        /**
         * @return the current distortion effect scale.
         * @since 1.8.4
         */
        public double getDistortionEffect() {
            return base.getDistortionEffectScale().getValue();
        }

        /**
         * @param val the new distortion effect scale
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setDistortionEffect(double val) {
            getBase(base.getDistortionEffectScale()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current fov effect scale.
         * @since 1.8.4
         */
        public double getFovEffect() {
            return base.getFovEffectScale().getValue();
        }

        /**
         * @param val the new fov effect scale
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setFovEffect(double val) {
            base.getFovEffectScale().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the monochrome logo is enabled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isMonochromeLogoEnabled() {
            return base.getMonochromeLogo().getValue();
        }

        /**
         * @param val whether to enable the monochrome logo or not
         * @return the current helper instance for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableMonochromeLogo(boolean val) {
            base.getMonochromeLogo().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if lighting flashes are hidden, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean areLightningFlashesHidden() {
            return base.getHideLightningFlashes().getValue();
        }

        /**
         * @param val the new fov value
         * @return self for chaining.
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setFovEffect(boolean val) {
            getBase(base.getHideLightningFlashes()).forceSetValue(val);
            return this;
        }

    }

    /**
     * @return 0: off, 2: fancy
     * @since 1.1.7
     * @deprecated use {@link VideoOptionsHelper#getCloudsMode()} instead.
     */
    @Deprecated
    @DocletReplaceReturn("Trit")
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
     * @param mode 0: off, 2: fancy
     * @return
     * @since 1.1.7
     * @deprecated use {@link VideoOptionsHelper#setCloudsMode(String)} instead.
     */
    @Deprecated
    @DocletReplaceParams("mode: Trit")
    public OptionsHelper setCloudMode(int mode) {
        switch (mode) {
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
     * @return
     * @since 1.1.7
     * @deprecated use {@link VideoOptionsHelper#getGraphicsMode()} instead.
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
     * @param mode 0: fast, 2: fabulous
     * @return
     * @since 1.1.7
     * @deprecated use {@link VideoOptionsHelper#setGraphicsMode(String)} instead.
     */
    @Deprecated
    public OptionsHelper setGraphicsMode(int mode) {
        switch (mode) {
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
     * @return
     * @since 1.1.7
     * @deprecated use {@link SkinOptionsHelper#isRightHanded()} instead.
     */
    @Deprecated
    public boolean isRightHanded() {
        return base.getMainArm().getValue() == Arm.RIGHT;
    }

    /**
     * @param val
     * @since 1.1.7
     * @deprecated use {@link SkinOptionsHelper#toggleMainHand(String)} instead.
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
     * @return
     * @since 1.1.7
     * @deprecated use {@link VideoOptionsHelper#getRenderDistance()} instead.
     */
    @Deprecated
    public int getRenderDistance() {
        return base.getViewDistance().getValue();
    }

    /**
     * @param d
     * @since 1.1.7
     * @deprecated use {@link VideoOptionsHelper#setRenderDistance(int)} instead.
     */
    @Deprecated
    public OptionsHelper setRenderDistance(int d) {
        ((MixinSimpleOption) (Object) base.getViewDistance()).forceSetValue(d);
        return this;
    }

    /**
     * @since 1.3.0 normal values for gamma are between {@code 0} and {@code 1}
     * @deprecated use {@link VideoOptionsHelper#getGamma()} instead.
     */
    @Deprecated
    public double getGamma() {
        return base.getGamma().getValue();
    }

    /**
     * @since 1.3.0 normal values for gamma are between {@code 0} and {@code 1}
     * @deprecated use {@link VideoOptionsHelper#setGamma(double)} instead.
     */
    @Deprecated
    public OptionsHelper setGamma(double gamma) {
        ((MixinSimpleOption) (Object) base.getGamma()).forceSetValue(gamma);
        return this;
    }

    /**
     * @param vol
     * @since 1.3.1
     * @deprecated use {@link MusicOptionsHelper#setMasterVolume(double)} instead.
     */
    @Deprecated
    public OptionsHelper setVolume(double vol) {
        base.getSoundVolumeOption(SoundCategory.MASTER).setValue(vol);
        return this;
    }

    /**
     * set volume by category.
     *
     * @param category
     * @param volume
     * @since 1.3.1
     * @deprecated use {@link MusicOptionsHelper#setVolume(String, double)} instead.
     */
    @Deprecated
    @DocletReplaceParams("category: SoundCategory, volume: double")
    public OptionsHelper setVolume(String category, double volume) {
        base.getSoundVolumeOption(Arrays.stream(SoundCategory.values()).filter(e -> e.getName().equals(category)).findFirst().orElseThrow(() -> new IllegalArgumentException("unknown sound category"))).setValue(volume);
        return this;
    }

    /**
     * @return
     * @since 1.3.1
     * @deprecated use {@link MusicOptionsHelper#getVolumes()} instead.
     */
    @Deprecated
    @DocletReplaceReturn("JavaMap<SoundCategory, float>")
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
     * @param scale
     * @since 1.3.1
     * @deprecated use {@link VideoOptionsHelper#setGuiScale(int)} instead.
     */
    @Deprecated
    public OptionsHelper setGuiScale(int scale) {
        base.getGuiScale().setValue(scale);
        mc.execute(mc::onResolutionChanged);
        return this;
    }

    /**
     * @return gui scale, {@code 0} for auto.
     * @since 1.3.1
     * @deprecated use {@link VideoOptionsHelper#getGuiScale()} instead.
     */
    @Deprecated
    public int getGuiScale() {
        return base.getGuiScale().getValue();
    }

    /**
     * @param category
     * @return
     * @since 1.3.1
     * @deprecated use {@link MusicOptionsHelper#getVolume(String)} instead.
     */
    @Deprecated
    @DocletReplaceParams("category: SoundCategory")
    public float getVolume(String category) {
        return base.getSoundVolume(SOUND_CATEGORY_MAP.get(category));
    }

}
