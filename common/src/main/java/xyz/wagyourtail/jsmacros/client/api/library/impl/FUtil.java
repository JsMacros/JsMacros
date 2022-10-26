package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementManagerHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementProgressHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BossBarHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ChatHudLineHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CheckBoxWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ChunkHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClickableWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CyclingButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.DirectionHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.FluidStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.FoodComponentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.FormattingHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.FullOptionsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.LockButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.PlayerAbilitiesHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.PlayerListEntryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.RecipeHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ScoreboardObjectiveHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ScoreboardsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ServerInfoHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.SliderWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StyleHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.SuggestionsBuilderHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TeamHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.util.NameUtil;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.wagyourgui.elements.CheckBox;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Library("Util")
@SuppressWarnings("unused")
public class FUtil extends BaseLibrary {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Creates a java {@link ArrayList}.
     *
     * @return a java ArrayList.
     *
     * @since 1.8.4
     */
    public ArrayList<?> createArrayList() {
        return new ArrayList<>();
    }

    /**
     * Creates a java {@link ArrayList}.
     *
     * @param array the array to add to the list
     * @param <T>   the type of the array
     * @return a java ArrayList from the given array.
     *
     * @since 1.8.4
     */
    public <T> ArrayList<T> createArrayList(T[] array) {
        return Lists.newArrayList(array);
    }

    /**
     * Creates a java {@link HashMap}.
     *
     * @return a java HashMap.
     *
     * @since 1.8.4
     */
    public HashMap<?, ?> createHashMap() {
        return new HashMap<>();
    }

    /**
     * Creates a java {@link HashSet}.
     *
     * @return a java HashSet.
     *
     * @since 1.8.4
     */
    public HashSet<?> createHashSet() {
        return new HashSet<>();
    }

    /**
     * @param raw the object to wrap
     * @return the correct instance of {@link BaseHelper} for the given object if it exists and
     *         {@code null} otherwise.
     *
     * @since 1.8.4
     */
    public BaseHelper<?> getHelperFromRaw(Object raw) {
        // Didn't implement CommandNodeHelper, TradeOfferHelper, ModContainerHelper
        if (raw instanceof BossBar) {
            return new BossBarHelper((BossBar) raw);
        } else if (raw instanceof ChatHudLine) {
            return new ChatHudLineHelper(((ChatHudLine) raw), mc.inGameHud.getChatHud());
        } else if (raw instanceof Chunk) {
            return new ChunkHelper(((Chunk) raw));
        } else if (raw instanceof CommandContext<?>) {
            return new CommandContextHelper(((CommandContext<?>) raw));
        } else if (raw instanceof Direction) {
            return new DirectionHelper(((Direction) raw));
        } else if (raw instanceof Enchantment) {
            return new EnchantmentHelper(((Enchantment) raw));
        } else if (raw instanceof FoodComponent) {
            return new FoodComponentHelper(((FoodComponent) raw));
        } else if (raw instanceof ItemStack) {
            return new ItemStackHelper(((ItemStack) raw));
        } else if (raw instanceof GameOptions) {
            return new FullOptionsHelper(((GameOptions) raw));
        } else if (raw instanceof PacketByteBuf) {
            return new PacketByteBufferHelper(((PacketByteBuf) raw));
        } else if (raw instanceof Packet<?>) {
            return new PacketByteBufferHelper(((Packet<?>) raw));
        } else if (raw instanceof NbtElement) {
            return NBTElementHelper.resolve(((NbtElement) raw));
        } else if (raw instanceof PlayerAbilities) {
            return new PlayerAbilitiesHelper(((PlayerAbilities) raw));
        } else if (raw instanceof PlayerListEntry) {
            return new PlayerListEntryHelper(((PlayerListEntry) raw));
        } else if (raw instanceof Recipe<?>) {
            return new RecipeHelper(((Recipe<?>) raw), -1);
        } else if (raw instanceof ScoreboardObjective) {
            return new ScoreboardObjectiveHelper(((ScoreboardObjective) raw));
        } else if (raw instanceof Scoreboard) {
            return new ScoreboardsHelper(((Scoreboard) raw));
        } else if (raw instanceof ServerInfo) {
            return new ServerInfoHelper(((ServerInfo) raw));
        } else if (raw instanceof StatHandler) {
            return new StatsHelper(((StatHandler) raw));
        } else if (raw instanceof StatusEffectInstance) {
            return new StatusEffectHelper(((StatusEffectInstance) raw));
        } else if (raw instanceof Style) {
            return new StyleHelper(((Style) raw));
        } else if (raw instanceof SuggestionsBuilder) {
            return new SuggestionsBuilderHelper(((SuggestionsBuilder) raw));
        } else if (raw instanceof Team) {
            return new TeamHelper(((Team) raw));
        } else if (raw instanceof Text) {
            return new TextHelper(((Text) raw));
        } else if (raw instanceof Formatting) {
            return new FormattingHelper(((Formatting) raw));
        }

        if (raw instanceof Entity) {
            return EntityHelper.create(((Entity) raw));
        }

        if (raw instanceof Advancement) {
            return new AdvancementHelper(((Advancement) raw));
        } else if (raw instanceof AdvancementManager) {
            return new AdvancementManagerHelper(((AdvancementManager) raw));
        } else if (raw instanceof AdvancementProgress) {
            return new AdvancementProgressHelper(((AdvancementProgress) raw));
        }

        if (raw instanceof Block) {
            return new BlockHelper(((Block) raw));
        } else if (raw instanceof BlockPos) {
            return new BlockPosHelper(((BlockPos) raw));
        } else if (raw instanceof BlockState) {
            return new BlockStateHelper(((BlockState) raw));
        } else if (raw instanceof FluidState) {
            return new FluidStateHelper(((FluidState) raw));
        }

        if (raw instanceof CheckBox) {
            return new CheckBoxWidgetHelper(((CheckBox) raw));
        } else if (raw instanceof CyclingButtonWidget<?>) {
            return new CyclingButtonWidgetHelper<>(((CyclingButtonWidget<?>) raw));
        } else if (raw instanceof LockButtonWidget) {
            return new LockButtonWidgetHelper(((LockButtonWidget) raw));
        } else if (raw instanceof Slider) {
            return new SliderWidgetHelper(((Slider) raw));
        } else if (raw instanceof TextFieldWidget) {
            return new TextFieldWidgetHelper(((TextFieldWidget) raw));
        } else if (raw instanceof ClickableWidget) {
            return new ClickableWidgetHelper<>(((ClickableWidget) raw));
        }
        return null;
    }

    /**
     * @param uri the uri to open
     * @since 1.8.4
     */
    public void openLink(String uri) throws URISyntaxException {
        Util.getOperatingSystem().open(new URI(uri));
    }

    /**
     * @param path the path top open, relative the config folder
     * @since 1.8.4
     */
    public void openFile(String path) {
        Util.getOperatingSystem().open(JsMacros.core.config.configFolder.toPath().resolve(path).toFile());
    }

    /**
     * @param array the array to convert
     * @return the String representation of the given array.
     *
     * @since 1.8.4
     */
    public String arrayToString(Object[] array) {
        return Arrays.toString(array);
    }

    /**
     * This method will convert any objects hold in the array data to Strings and should be used for
     * multidimensional arrays.
     *
     * @param array the array to convert
     * @return the String representation of the given array.
     *
     * @since 1.8.4
     */
    public String arrayDeepToString(Object[] array) {
        return Arrays.deepToString(array);
    }

    /**
     * @param obj           the object to get the fields of
     * @param includeStatic whether to include static fields
     * @return a map of all the fields and their values in the given object.
     *
     * @since 1.8.4
     */
    public Map<String, Object> toFieldMap(Object obj, boolean includeStatic) {
        Map<String, Object> fields = new HashMap<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && !includeStatic) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    fields.put(field.getName(), field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Copies the text to the clipboard.
     *
     * @param text the text to copy
     * @since 1.8.4
     */
    public void copyToClipboard(String text) {
        SelectionManager.setClipboard(mc, text);
    }

    /**
     * @return the text from the clipboard.
     *
     * @since 1.8.4
     */
    public String getClipboard() {
        return SelectionManager.getClipboard(mc);
    }

    /**
     * Hashes the given string with sha-256.
     *
     * @param message the message to hash
     * @return the hashed message.
     *
     * @since 1.8.4
     */
    public String hashString(String message) {
        return DigestUtils.sha256Hex(message);
    }

    /**
     * Hashes the given string with sha-256 the selected algorithm.
     *
     * @param message   the message to hash
     * @param algorithm sha1 | sha256 | sha384 | sha512 | md2 | md5
     * @return the hashed message.
     *
     * @since 1.8.4
     */
    public String hashString(String message, String algorithm) {
        switch (algorithm) {
            case "sha256":
                return DigestUtils.sha256Hex(message);
            case "sha512":
                return DigestUtils.sha512Hex(message);
            case "sha1":
                return DigestUtils.sha1Hex(message);
            case "sha384":
                return DigestUtils.sha384Hex(message);
            case "md2":
                return DigestUtils.md2Hex(message);
            case "md5":
                return DigestUtils.md5Hex(message);
            default:
                return message;
        }
    }

    /**
     * Encodes the given string with Base64.
     *
     * @param message the message to encode
     * @return the encoded message.
     *
     * @since 1.8.4
     */
    public String encode(String message) {
        return new String(Base64.encodeBase64(message.getBytes()));
    }

    /**
     * Decodes the given string with Base64.
     *
     * @param message the message to decode
     * @return the decoded message.
     *
     * @since 1.8.4
     */
    public String decode(String message) {
        return new String(Base64.decodeBase64(message.getBytes()));
    }

    /**
     * Returns a {@link SplittableRandom}.
     *
     * @return a SplittableRandom.
     *
     * @since 1.8.4
     */
    public SplittableRandom getRandom() {
        return new SplittableRandom();
    }

    /**
     * Returns {@link SplittableRandom}, initialized with the seed to get identical sequences of
     * values at all times.
     *
     * @param seed the seed
     * @return a SplittableRandom.
     *
     * @since 1.8.4
     */
    public SplittableRandom getRandom(long seed) {
        return new SplittableRandom(seed);
    }

    /**
     * @param identifier the String representation of the identifier, with the namespace and path
     * @return the raw minecraft Identifier.
     *
     * @since 1.8.4
     */
    public Identifier getIdentifier(String identifier) {
        return RegistryHelper.parseIdentifier(identifier);
    }

    /**
     * Tries to guess the name of the sender of a given message.
     *
     * @param text the text to check
     * @return the name of the sender or null if it couldn't be guessed.
     *
     * @since 1.8.4
     */
    public String guessName(TextHelper text) {
        return guessName(text.getStringStripFormatting());
    }

    /**
     * Tries to guess the name of the sender of a given message.
     *
     * @param text the text to check
     * @return the name of the sender or null if it couldn't be guessed.
     *
     * @since 1.8.4
     */
    public String guessName(String text) {
        List<String> names = guessNameAndRoles(text);
        return names.isEmpty() ? null : names.get(0);
    }

    /**
     * Tries to guess the name, as well as the titles and roles of the sender of the given message.
     *
     * @param text the text to check
     * @return a list of names, titles and roles of the sender or an empty list if it couldn't be
     *         guessed.
     *
     * @since 1.8.4
     */
    public List<String> guessNameAndRoles(TextHelper text) {
        return guessNameAndRoles(text.getStringStripFormatting());
    }

    /**
     * Tries to guess the name, as well as the titles and roles of the sender of the given message.
     *
     * @param text the text to check
     * @return a list of names, titles and roles of the sender or an empty list if it couldn't be
     *         guessed.
     *
     * @since 1.8.4
     */
    public List<String> guessNameAndRoles(String text) {
        return NameUtil.guessNameAndRoles(text);
    }

}