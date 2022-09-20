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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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
import xyz.wagyourtail.jsmacros.client.api.classes.util.NameUtil;
import xyz.wagyourtail.jsmacros.client.api.helpers.BossBarHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ChatHudLineHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ChunkHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.DirectionHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.PlayerAbilitiesHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.PlayerListEntryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.RecipeHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ScoreboardObjectiveHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ScoreboardsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ServerInfoHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StyleHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.SuggestionsBuilderHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TeamHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.advancement.AdvancementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.advancement.AdvancementManagerHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.advancement.AdvancementProgressHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.FluidStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.CheckBoxWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.CyclingButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.LockButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.SliderWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.wagyourgui.elements.CheckBox;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * @author Etheradon
 * @since 1.9.0
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
     * @since 1.9.0
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
     * @since 1.9.0
     */
    public <T> ArrayList<T> createArrayList(T[] array) {
        return Lists.newArrayList(array);
    }

    /**
     * Creates a java {@link HashMap}.
     *
     * @return a java HashMap.
     *
     * @since 1.9.0
     */
    public HashMap<?, ?> createHashMap() {
        return new HashMap<>();
    }

    /**
     * Creates a java {@link HashSet}.
     *
     * @return a java HashSet.
     *
     * @since 1.9.0
     */
    public HashSet<?> createHashSet() {
        return new HashSet<>();
    }

    /**
     * Creates a java {@link LinkedList}.
     *
     * @return a java LinkedList.
     *
     * @since 1.9.0
     */
    public LinkedList<?> createLinkedList() {
        return new LinkedList<>();
    }

    /**
     * @param obj the object to wrap
     * @return the correct instance of {@link BaseHelper} for the given object if it exists and
     *         {@code null} otherwise.
     *
     * @since 1.9.0
     */
    public BaseHelper<?> getHelperFromRaw(Object obj) {
        //didn't implement CommandNodeHelper, TradeOfferHelper
        if (obj instanceof BossBar bossBar) {
            return new BossBarHelper(bossBar);
        } else if (obj instanceof ChatHudLine chatHudLine) {
            return new ChatHudLineHelper(chatHudLine, mc.inGameHud.getChatHud());
        } else if (obj instanceof Chunk chunk) {
            return new ChunkHelper(chunk);
        } else if (obj instanceof CommandContext<?> commandContext) {
            return new CommandContextHelper(commandContext);
        } else if (obj instanceof Direction direction) {
            return new DirectionHelper(direction);
        } else if (obj instanceof Enchantment enchantment) {
            return new EnchantmentHelper(enchantment);
        } else if (obj instanceof ItemStack itemStack) {
            return new ItemStackHelper(itemStack);
        } else if (obj instanceof NbtElement nbtElement) {
            return NBTElementHelper.resolve(nbtElement);
        } else if (obj instanceof GameOptions gameOptions) {
            return new OptionsHelper(gameOptions);
        } else if (obj instanceof PlayerAbilities playerAbilities) {
            return new PlayerAbilitiesHelper(playerAbilities);
        } else if (obj instanceof PlayerListEntry playerListEntry) {
            return new PlayerListEntryHelper(playerListEntry);
        } else if (obj instanceof Recipe<?> recipe) {
            return new RecipeHelper(recipe, -1);
        } else if (obj instanceof ScoreboardObjective scoreboardObjective) {
            return new ScoreboardObjectiveHelper(scoreboardObjective);
        } else if (obj instanceof Scoreboard scoreboard) {
            return new ScoreboardsHelper(scoreboard);
        } else if (obj instanceof ServerInfo serverInfo) {
            return new ServerInfoHelper(serverInfo);
        } else if (obj instanceof StatHandler statHandler) {
            return new StatsHelper(statHandler);
        } else if (obj instanceof StatusEffectInstance statusEffectInstance) {
            return new StatusEffectHelper(statusEffectInstance);
        } else if (obj instanceof Style style) {
            return new StyleHelper(style);
        } else if (obj instanceof SuggestionsBuilder suggestionsBuilder) {
            return new SuggestionsBuilderHelper(suggestionsBuilder);
        } else if (obj instanceof Team team) {
            return new TeamHelper(team);
        } else if (obj instanceof Text text) {
            return new TextHelper(text);
        }

        if (obj instanceof Entity entity) {
            return EntityHelper.create(entity);
        }

        if (obj instanceof Advancement advancement) {
            return new AdvancementHelper(advancement);
        } else if (obj instanceof AdvancementManager advancementManager) {
            return new AdvancementManagerHelper(advancementManager);
        } else if (obj instanceof AdvancementProgress advancementProgress) {
            return new AdvancementProgressHelper(advancementProgress);
        }

        if (obj instanceof Block block) {
            return new BlockHelper(block);
        } else if (obj instanceof BlockPos blockPos) {
            return new BlockPosHelper(blockPos);
        } else if (obj instanceof BlockState blockState) {
            return new BlockStateHelper(blockState);
        } else if (obj instanceof FluidState fluidState) {
            return new FluidStateHelper(fluidState);
        }

        if (obj instanceof CheckBox checkBox) {
            return new CheckBoxWidgetHelper(checkBox);
        } else if (obj instanceof CyclingButtonWidget<?> cyclingButtonWidget) {
            return new CyclingButtonWidgetHelper<>(cyclingButtonWidget);
        } else if (obj instanceof LockButtonWidget lockButtonWidget) {
            return new LockButtonWidgetHelper(lockButtonWidget);
        } else if (obj instanceof Slider slider) {
            return new SliderWidgetHelper(slider);
        } else if (obj instanceof TextFieldWidget textFieldWidget) {
            return new TextFieldWidgetHelper(textFieldWidget);
        } else if (obj instanceof ClickableWidget clickableWidget) {
            return new ButtonWidgetHelper<>(clickableWidget);
        }
        return null;
    }

    /**
     * @param text the text to check
     * @return the pixel width of the given text for the current font renderer.
     *
     * @since 1.9.0
     */
    public int getTextWidth(TextHelper text) {
        return mc.textRenderer.getWidth(text.getRaw());
    }

    /**
     * @param text the text to check
     * @return the pixel width of the given text for the current font renderer.
     *
     * @since 1.9.0
     */
    public int getTextWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }

    /**
     * @param uri the uri to open
     * @since 1.9.0
     */
    public void openLink(String uri) throws URISyntaxException {
        Util.getOperatingSystem().open(new URI(uri));
    }

    /**
     * @param path the path top open, relative the config folder
     * @since 1.9.0
     */
    public void openFile(String path) {
        Util.getOperatingSystem().open(JsMacros.core.config.configFolder.toPath().resolve(path).toFile());
    }

    /**
     * @param array the array to convert
     * @return the String representation of the given array.
     *
     * @since 1.9.0
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
     * @since 1.9.0
     */
    public String arrayDeepToString(Object[] array) {
        return Arrays.deepToString(array);
    }

    /**
     * Copies the text to the clipboard.
     *
     * @param text the text to copy
     * @since 1.9.0
     */
    public void copyToClipboard(String text) {
        SelectionManager.setClipboard(mc, text);
    }

    /**
     * @return the text from the clipboard
     *
     * @since 1.9.0
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
     * @since 1.9.0
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
     * @since 1.9.0
     */
    public String hashString(String message, String algorithm) {
        return switch (algorithm) {
            case "sha256" -> DigestUtils.sha256Hex(message);
            case "sha512" -> DigestUtils.sha512Hex(message);
            case "sha1" -> DigestUtils.sha1Hex(message);
            case "sha384" -> DigestUtils.sha384Hex(message);
            case "md2" -> DigestUtils.md2Hex(message);
            case "md5" -> DigestUtils.md5Hex(message);
            default -> message;
        };
    }

    /**
     * Encodes the given string with Base64.
     *
     * @param message the message to encode
     * @return the encoded message.
     *
     * @since 1.9.0
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
     * @since 1.9.0
     */
    public String decode(String message) {
        return new String(Base64.decodeBase64(message.getBytes()));
    }

    /**
     * Returns a {@link SplittableRandom}.
     *
     * @return a SplittableRandom
     *
     * @since 1.9.0
     */
    public SplittableRandom getRandom() {
        return new SplittableRandom();
    }

    /**
     * Returns {@link SplittableRandom}, initialized with the seed to get identical sequences of
     * values at all times.
     *
     * @param seed the seed
     * @return a SplittableRandom
     *
     * @since 1.9.0
     */
    public SplittableRandom getRandom(long seed) {
        return new SplittableRandom(seed);
    }

    /**
     * @param identifier the String representation of the identifier, with the namespace and path
     * @return the raw minecraft Identifier.
     *
     * @since 1.9.0
     */
    public Identifier getIdentifier(String identifier) {
        return new Identifier(identifier);
    }

    /**
     * Tries to guess the name of the sender of a given message.
     *
     * @param text the text to check
     * @return the name of the sender or null if it couldn't be guessed.
     *
     * @since 1.9.0
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
     * @since 1.9.0
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
     * @since 1.9.0
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
     * @since 1.9.0
     */
    public List<String> guessNameAndRoles(String text) {
        return NameUtil.guessNameAndRoles(text);
    }

}
