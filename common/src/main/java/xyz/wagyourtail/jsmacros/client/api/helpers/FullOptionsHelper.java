package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.AoMode;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Arm;
import net.minecraft.world.Difficulty;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinSimpleOption;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FullOptionsHelper extends BaseHelper<GameOptions> {

    private static final Map<String, SoundCategory> SOUND_CATEGORY_MAP = Arrays.stream(SoundCategory.values()).collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ResourcePackManager rpm = mc.getResourcePackManager();

    public final SkinOptionsHelper skin = new SkinOptionsHelper(this);
    public final VideoOptionsHelper video = new VideoOptionsHelper(this);
    public final MusicOptionsHelper music = new MusicOptionsHelper(this);
    public final ControlOptionsHelper control = new ControlOptionsHelper(this);
    public final ChatOptionsHelper chat = new ChatOptionsHelper(this);
    public final AccessibilityOptionsHelper accessibility = new AccessibilityOptionsHelper(this);

    public FullOptionsHelper(GameOptions options) {
        super(options);
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public SkinOptionsHelper getSkinOptions() {
        return skin;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public VideoOptionsHelper getVideoOptions() {
        return video;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public MusicOptionsHelper getMusicOptions() {
        return music;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public ControlOptionsHelper getControlOptions() {
        return control;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public ChatOptionsHelper getChatOptions() {
        return chat;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public AccessibilityOptionsHelper getAccessibilityOptions() {
        return accessibility;
    }

    public FullOptionsHelper saveOptions() {
        base.write();
        return this;
    }

    /**
     * @return list of names of resource packs.
     *
     * @since 1.1.7
     */
    public List<String> getResourcePacks() {
        return new ArrayList<>(rpm.getNames());
    }

    /**
     * @return list of names of enabled resource packs.
     *
     * @since 1.2.0
     */
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<>(rpm.getEnabledNames());
    }

    /**
     * Set the enabled resource packs to the provided list.
     *
     * @param enabled
     * @return
     *
     * @since 1.2.0
     */
    public FullOptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = Arrays.stream(enabled).distinct().toList();
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
     * @return
     *
     * @since 1.1.7
     */
    public int getFov() {
        return base.getFov().getValue();
    }

    /**
     * @param fov
     * @return
     *
     * @since 1.1.7
     */
    public FullOptionsHelper setFov(int fov) {
        base.getFov().setValue(fov);
        return this;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public int getViewDistance() {
        return base.getViewDistance().getValue();
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public String getLanguage() {
        return base.language;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public FullOptionsHelper setLanguage(String languageCode) {
        LanguageDefinition language = MinecraftClient.getInstance().getLanguageManager().getLanguage(languageCode);
        base.language = language.getCode();
        MinecraftClient.getInstance().reloadResources();
        base.write();
        return this;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public String getDifficulty() {
        return mc.world.getDifficulty().getName();
    }

    /**
     * @param name
     * @return
     *
     * @since 1.8.4
     */
    public FullOptionsHelper setDifficulty(String name) {
        if (mc.isIntegratedServerRunning()) {
            mc.getServer().setDifficulty(Difficulty.byName(name), true);
        }
        return this;
    }

    /**
     * @param ordinal
     * @return
     *
     * @since 1.8.4
     */
    public FullOptionsHelper setDifficulty(int ordinal) {
        if (mc.isIntegratedServerRunning()) {
            mc.getServer().setDifficulty(Difficulty.byOrdinal(ordinal), true);
        }
        return this;
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public boolean isDifficultyLocked() {
        return MinecraftClient.getInstance().world.getLevelProperties().isDifficultyLocked();
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public FullOptionsHelper lockDifficulty() {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
        return this;
    }

    private MixinSimpleOption getBase(SimpleOption option) {
        return (MixinSimpleOption) (Object) option;
    }

    public class SkinOptionsHelper {

        public final FullOptionsHelper parent;

        public SkinOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isCapeActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.CAPE);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isJacketActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.JACKET);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isLeftSleeveActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isRightSleeveActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isLeftPantsActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isRightPantsActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isHatActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.HAT);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isRightHand() {
            return base.getMainArm().getValue() == Arm.RIGHT;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleCape(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.CAPE, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleJacket(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.JACKET, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleLeftSleeve(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.LEFT_SLEEVE, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleRightSleeve(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleLeftPanTs(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleRightPants(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleHat(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.HAT, val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleMainHand(String hand) {
            base.getMainArm().setValue(hand.equals("left") ? Arm.LEFT : Arm.RIGHT);
            return this;
        }

    }

    public class VideoOptionsHelper {

        public final FullOptionsHelper parent;

        public VideoOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getFullscreenResolution() {
            return base.fullscreenResolution;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public int getBiomeBlendRadius() {
            return base.getBiomeBlendRadius().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setBiomeBlendRadius(int radius) {
            base.getBiomeBlendRadius().setValue(radius);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getGraphicsMode() {
            return switch (base.getGraphicsMode().getValue()) {
                case FAST -> "fast";
                case FANCY -> "fancy";
                case FABULOUS -> "fabulous";
                default -> "";
            };
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setGraphicsMode(String mode) {
            base.getGraphicsMode().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "FAST" -> GraphicsMode.FAST;
                case "FANCY" -> GraphicsMode.FANCY;
                case "FABULOUS" -> GraphicsMode.FABULOUS;
                default -> base.getGraphicsMode().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getChunkBuilderMode() {
            return switch (base.getChunkBuilderMode().getValue()) {
                case NONE -> "none";
                case NEARBY -> "nearby";
                case PLAYER_AFFECTED -> "player_affected";
                default -> "";
            };
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setChunkBuilderMode(String mode) {
            base.getChunkBuilderMode().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "NONE" -> ChunkBuilderMode.NONE;
                case "NEARBY" -> ChunkBuilderMode.NEARBY;
                case "PLAYER_AFFECTED" -> ChunkBuilderMode.PLAYER_AFFECTED;
                default -> base.getChunkBuilderMode().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getSmoothLightningMode() {
            return switch (base.getAo().getValue()) {
                case OFF -> "off";
                case MIN -> "min";
                case MAX -> "max";
                default -> "";
            };
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setSmoothLightningMode(String mode) {
            base.getAo().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> AoMode.OFF;
                case "MIN" -> AoMode.MIN;
                case "MAX" -> AoMode.MAX;
                default -> base.getAo().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public int getRenderDistance() {
            return base.getViewDistance().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setRenderDistance(int radius) {
            base.getViewDistance().setValue(radius);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public int getSimulationDistance() {
            return base.getSimulationDistance().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setSimulationDistance(int radius) {
            base.getSimulationDistance().setValue(radius);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public int getMaxFps() {
            return base.getMaxFps().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setMaxFps(int maxFps) {
            base.getMaxFps().setValue(maxFps);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isVsyncEnabled() {
            return base.getEnableVsync().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableVsync(boolean val) {
            base.getEnableVsync().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isViewBobbingEnabled() {
            return base.getBobView().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableViewBobbing(boolean val) {
            base.getBobView().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public int getGuiScale() {
            return base.getGuiScale().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setGuiScale(int scale) {
            base.getGuiScale().setValue(scale);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getAttackIndicatorType() {
            return switch (base.getAttackIndicator().getValue()) {
                case OFF -> "off";
                case CROSSHAIR -> "crosshair";
                case HOTBAR -> "hotbar";
                default -> "";
            };
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setAttackIndicatorType(String type) {
            base.getAttackIndicator().setValue(switch (type.toUpperCase(Locale.ROOT)) {
                case "OFF" -> AttackIndicator.OFF;
                case "CROSSHAIR" -> AttackIndicator.CROSSHAIR;
                case "HOTBAR" -> AttackIndicator.HOTBAR;
                default -> base.getAttackIndicator().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getGamma() {
            return getBrightness();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setGamma(double gamma) {
            return setBrightness(gamma);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getBrightness() {
            return base.getGamma().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setBrightness(double gamma) {
            base.getGamma().setValue(gamma);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getCloudsMode() {
            return switch (base.getCloudRenderMode().getValue()) {
                case OFF -> "off";
                case FAST -> "fast";
                case FANCY -> "fancy";
                default -> "";
            };
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setCloudsMode(String mode) {
            base.getCloudRenderMode().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> CloudRenderMode.OFF;
                case "FAST" -> CloudRenderMode.FAST;
                case "FANCY" -> CloudRenderMode.FANCY;
                default -> base.getCloudRenderMode().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isFullscreen() {
            return base.getFullscreen().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setBrightness(boolean fullscreen) {
            base.getFullscreen().setValue(fullscreen);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getParticleMode() {
            return switch (base.getParticles().getValue()) {
                case MINIMAL -> "minimal";
                case DECREASED -> "decreased";
                case ALL -> "all";
                default -> "";
            };
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setParticleMode(String mode) {
            base.getParticles().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "MINIMAL" -> ParticlesMode.MINIMAL;
                case "DECREASED" -> ParticlesMode.DECREASED;
                case "ALL" -> ParticlesMode.ALL;
                default -> base.getParticles().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public int getMipMapLevels() {
            return base.getMipmapLevels().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setMipMapLevels(int val) {
            base.getMipmapLevels().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areEntityShadowsEnabled() {
            return base.getEntityShadows().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableEntityShadows(boolean val) {
            base.getEntityShadows().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getDistortionEffects() {
            return base.getDistortionEffectScale().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setDistortionEffects(double val) {
            base.getDistortionEffectScale().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getEntityDistance() {
            return base.getEntityDistanceScaling().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setEntityDistance(double val) {
            base.getEntityDistanceScaling().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getFovEffects() {
            return base.getFovEffectScale().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setFovEffects(double val) {
            base.getFovEffectScale().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isAutosaveIndicatorEnabled() {
            return base.getShowAutosaveIndicator().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableAutosaveIndicator(boolean val) {
            base.getShowAutosaveIndicator().setValue(val);
            return this;
        }

    }

    public class MusicOptionsHelper {

        public final FullOptionsHelper parent;

        public MusicOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getMasterVolume() {
            return base.getSoundVolume(SoundCategory.MASTER);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setMasterVolume(float volume) {
            base.setSoundVolume(SoundCategory.MASTER, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getMusicVolume() {
            return base.getSoundVolume(SoundCategory.MUSIC);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setMusicVolume(float volume) {
            base.setSoundVolume(SoundCategory.MUSIC, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getRecordsVolume() {
            return base.getSoundVolume(SoundCategory.RECORDS);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setRecordsVolume(float volume) {
            base.setSoundVolume(SoundCategory.RECORDS, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getWeatherVolume() {
            return base.getSoundVolume(SoundCategory.WEATHER);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setWeatherVolume(float volume) {
            base.setSoundVolume(SoundCategory.WEATHER, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getBlocksVolume() {
            return base.getSoundVolume(SoundCategory.BLOCKS);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setBlocksVolume(float volume) {
            base.setSoundVolume(SoundCategory.BLOCKS, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getHostileVolume() {
            return base.getSoundVolume(SoundCategory.HOSTILE);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setHostileVolume(float volume) {
            base.setSoundVolume(SoundCategory.HOSTILE, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getFriendlyVolume() {
            return base.getSoundVolume(SoundCategory.NEUTRAL);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setFriendlyVolume(float volume) {
            base.setSoundVolume(SoundCategory.NEUTRAL, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getPlayerVolume() {
            return base.getSoundVolume(SoundCategory.PLAYERS);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setPlayerVolume(float volume) {
            base.setSoundVolume(SoundCategory.PLAYERS, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getAmbientVolume() {
            return base.getSoundVolume(SoundCategory.AMBIENT);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setAmbientVolume(float volume) {
            base.setSoundVolume(SoundCategory.AMBIENT, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getVoiceVolume() {
            return base.getSoundVolume(SoundCategory.VOICE);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setVoiceVolume(float volume) {
            base.setSoundVolume(SoundCategory.VOICE, volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public float getVolume(String category) {
            return base.getSoundVolume(SOUND_CATEGORY_MAP.get(category));
        }

        /**
         * @return
         *
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
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setVolume(String category, float volume) {
            base.setSoundVolume(SOUND_CATEGORY_MAP.get(category), volume);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getSoundDevice() {
            return base.getSoundDevice().getValue();
        }

        /**
         * @return
         *
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

        public List<String> getAudioDevices() {
            return Stream.concat(Stream.of(""), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).toList();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areSubtitlesShown() {
            return base.getShowSubtitles().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper showSubtitles(boolean val) {
            base.getShowSubtitles().setValue(val);
            return this;
        }

    }

    public class ControlOptionsHelper {

        public final FullOptionsHelper parent;

        public ControlOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getMouseSensitivity() {
            return base.getMouseSensitivity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper setMouseSensitivity(double val) {
            base.getMouseSensitivity().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isMouseInverted() {
            return base.getInvertYMouse().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper invertMouse(boolean val) {
            base.getInvertYMouse().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getMouseWheelSensitivity() {
            return base.getMouseWheelSensitivity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper setMouseWheelSensitivity(double val) {
            base.getMouseWheelSensitivity().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isDiscreteScrollingEnabled() {
            return base.getDiscreteMouseScroll().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableDiscreteScrolling(boolean val) {
            base.getDiscreteMouseScroll().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isTouchscreenEnabled() {
            return base.getTouchscreen().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableTouchscreen(boolean val) {
            base.getTouchscreen().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isRawMouseInputEnabled() {
            return base.getRawMouseInput().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableRawMouseInput(boolean val) {
            base.getRawMouseInput().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isAutoJumpEnabled() {
            return base.getAutoJump().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableAutoJump(boolean val) {
            base.getAutoJump().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isSneakToggled() {
            return base.getSneakToggled().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper toggleSneak(boolean val) {
            base.getSneakToggled().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isSprintToggled() {
            return base.getSprintToggled().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper toggleSprint(boolean val) {
            base.getSprintToggled().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public KeyBinding[] getRawKeys() {
            return ArrayUtils.clone(base.allKeys);
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public List<String> getCategories() {
            return Arrays.stream(base.allKeys).map(KeyBinding::getCategory).distinct().toList();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public List<String> getKeys() {
            return Arrays.stream(base.allKeys).map(KeyBinding::getTranslationKey).toList();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public Map<String, String> getKeyBinds() {
            Map<String, String> keyBinds = new HashMap<>(base.allKeys.length);

            for (KeyBinding key : base.allKeys) {
                keyBinds.put(Text.translatable(key.getTranslationKey()).getString(), key.getBoundKeyLocalizedText().getString());
            }
            return keyBinds;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public Map<String, String> getKeyBindsByCategory(String category) {
            return getKeyBindsByCategory().get(category);
        }

        /**
         * @return
         *
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

        public final FullOptionsHelper parent;

        public ChatOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getChatVisibility() {
            String chatVisibilityKey = base.getChatVisibility().getValue().getTranslationKey();
            return chatVisibilityKey.substring(chatVisibilityKey.lastIndexOf('.'));
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatVisibility(String mode) {
            base.getChatVisibility().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "FULL" -> ChatVisibility.FULL;
                case "SYSTEM" -> ChatVisibility.SYSTEM;
                case "HIDDEN" -> ChatVisibility.HIDDEN;
                default -> base.getChatVisibility().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areColorsShown() {
            return base.getChatColors().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatVisibility(boolean val) {
            base.getChatColors().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areWebLinksEnabled() {
            return base.getChatLinks().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableWebLinks(boolean val) {
            base.getChatLinks().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isWebLinkPromptEnabled() {
            return base.getChatLinksPrompt().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableWebLinkPrompt(boolean val) {
            base.getChatLinksPrompt().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatOpacity() {
            return base.getChatOpacity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatOpacity(double val) {
            base.getChatOpacity().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setTextBackgroundOpacity(double val) {
            base.getTextBackgroundOpacity().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getTextBackgroundOpacity() {
            return base.getTextBackgroundOpacity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getTextSize() {
            return base.getChatScale().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setTextSize(double val) {
            base.getChatScale().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatLineSpacing() {
            return base.getChatLineSpacing().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatLineSpacing(double val) {
            base.getChatLineSpacing().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatDelay() {
            return base.getChatDelay().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatDelay(double val) {
            base.getChatDelay().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatWidth() {
            return base.getChatWidth().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatWidth(double val) {
            base.getChatWidth().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatFocusedHeight() {
            return base.getChatHeightFocused().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatFocusedHeight(double val) {
            base.getChatHeightFocused().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatUnfocusedHeight() {
            return base.getChatHeightUnfocused().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatUnfocusedHeight(double val) {
            base.getChatHeightUnfocused().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getNarratorMode() {
            String narratorKey = ((TranslatableTextContent) (base.getNarrator().getValue().getName().getContent())).getKey();
            return narratorKey.substring(narratorKey.lastIndexOf('.'));
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setNarratorMode(String mode) {
            base.getNarrator().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> NarratorMode.OFF;
                case "ALL" -> NarratorMode.ALL;
                case "CHAT" -> NarratorMode.CHAT;
                case "SYSTEM" -> NarratorMode.SYSTEM;
                default -> base.getNarrator().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areCommandSuggestionsEnabled() {
            return base.getAutoSuggestions().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableCommandSuggestions(boolean val) {
            base.getAutoSuggestions().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areMatchedNamesHidden() {
            return base.getHideMatchedNames().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableHideMatchedNames(boolean val) {
            base.getHideMatchedNames().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isDebugInfoReduced() {
            return base.getReducedDebugInfo().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper reduceDebugInfo(boolean val) {
            base.getReducedDebugInfo().setValue(val);
            return this;
        }

    }

    public class AccessibilityOptionsHelper {

        public final FullOptionsHelper parent;

        public AccessibilityOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public String getNarratorMode() {
            String narratorKey = ((TranslatableTextContent) (base.getNarrator().getValue().getName().getContent())).getKey();
            return narratorKey.substring(narratorKey.lastIndexOf('.'));
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setNarratorMode(String mode) {
            base.getNarrator().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> NarratorMode.OFF;
                case "ALL" -> NarratorMode.ALL;
                case "CHAT" -> NarratorMode.CHAT;
                case "SYSTEM" -> NarratorMode.SYSTEM;
                default -> base.getNarrator().getValue();
            });
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean areSubtitlesShown() {
            return base.getShowSubtitles().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper showSubtitles(boolean val) {
            base.getShowSubtitles().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setTextBackgroundOpacity(double val) {
            base.getTextBackgroundOpacity().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getTextBackgroundOpacity() {
            return base.getTextBackgroundOpacity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isBackgroundForChatOnly() {
            return base.getBackgroundForChatOnly().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableBackgroundForChatOnly(boolean val) {
            base.getBackgroundForChatOnly().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatOpacity() {
            return base.getChatOpacity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatOpacity(double val) {
            base.getChatOpacity().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatLineSpacing() {
            return base.getChatLineSpacing().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatLineSpacing(double val) {
            base.getChatLineSpacing().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getChatDelay() {
            return base.getChatDelay().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatDelay(double val) {
            base.getChatDelay().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isAutoJumpEnabled() {
            return base.getAutoJump().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableAutoJump(boolean val) {
            base.getAutoJump().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isSneakToggled() {
            return base.getSneakToggled().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper toggleSneak(boolean val) {
            base.getSneakToggled().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isSprintToggled() {
            return base.getSprintToggled().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper toggleSprint(boolean val) {
            base.getSprintToggled().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getDistortionEffect() {
            return base.getDistortionEffectScale().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setDistortionEffect(double val) {
            base.getDistortionEffectScale().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public double getFovEffect() {
            return base.getFovEffectScale().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setFovEffect(double val) {
            base.getFovEffectScale().setValue(val);
            return this;
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isMonochromeLogoEnabled() {
            return base.getMonochromeLogo().getValue();
        }

        /**
         * @param val 
         * @return the current helper instance for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableMonochromeLogo(boolean val) {
            base.getMonochromeLogo().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if lighting flashes are hidden, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean areLightningFlashesHidden() {
            return base.getHideLightningFlashes().getValue();
        }

        /**
         * @param val the new fov value
         * @return the current helper instance for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setFovEffect(boolean val) {
            getBase(base.getHideLightningFlashes()).forceSetValue(val);
            return this;
        }

    }

}
