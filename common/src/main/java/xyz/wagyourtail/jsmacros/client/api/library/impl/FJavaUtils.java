package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.GameOptions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementManagerHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementProgressHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.BossBarHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.ChatHudLineHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.CheckBoxWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.ChunkHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.ClickableWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.DirectionHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.FluidStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.FoodComponentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.FormattingHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.LockButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.PlayerAbilitiesHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.PlayerListEntryHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.RecipeHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.ScoreboardObjectiveHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.ScoreboardsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.ServerInfoHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.SliderWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatsHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.StyleHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.SuggestionsBuilderHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.TeamHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.wagyourgui.elements.CheckBox;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SplittableRandom;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Library("JavaUtils")
@SuppressWarnings("unused")
public class FJavaUtils extends BaseLibrary {

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
     * @param raw the object to wrap
     * @return the correct instance of {@link BaseHelper} for the given object if it exists and
     *         {@code null} otherwise.
     *
     * @since 1.8.4
     */
    public BaseHelper<?> getHelperFromRaw(Object raw) throws IOException {
        // Didn't implement CommandNodeHelper, TradeOfferHelper, ModContainerHelper
        if (raw instanceof Entity) {
            return EntityHelper.create(((Entity) raw));
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
            return new OptionsHelper(((GameOptions) raw));
        } else if (raw instanceof PacketByteBuf) {
            return new PacketByteBufferHelper(((PacketByteBuf) raw));
        } else if (raw instanceof Packet<?>) {
            return new PacketByteBufferHelper(((Packet<?>) raw));
        } else if (raw instanceof Tag) {
            return NBTElementHelper.resolve(((Tag) raw));
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

        if (raw instanceof Advancement) {
            return new AdvancementHelper(((Advancement) raw));
        } else if (raw instanceof AdvancementManager) {
            return new AdvancementManagerHelper(((AdvancementManager) raw));
        } else if (raw instanceof AdvancementProgress) {
            return new AdvancementProgressHelper(((AdvancementProgress) raw));
        }

        if (raw instanceof CheckBox) {
            return new CheckBoxWidgetHelper(((CheckBox) raw));
        } else if (raw instanceof LockButtonWidget) {
            return new LockButtonWidgetHelper(((LockButtonWidget) raw));
        } else if (raw instanceof Slider) {
            return new SliderWidgetHelper(((Slider) raw));
        } else if (raw instanceof TextFieldWidget) {
            return new TextFieldWidgetHelper(((TextFieldWidget) raw));
        } else if (raw instanceof AbstractButtonWidget) {
            return new ClickableWidgetHelper<>(((AbstractButtonWidget) raw));
        }
        return null;
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

}