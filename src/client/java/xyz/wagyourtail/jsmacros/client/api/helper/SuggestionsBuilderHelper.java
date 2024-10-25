package xyz.wagyourtail.jsmacros.client.api.helper;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class SuggestionsBuilderHelper extends BaseHelper<SuggestionsBuilder> {
    public SuggestionsBuilderHelper(SuggestionsBuilder base) {
        super(base);
    }

    public String getInput() {
        return base.getInput();
    }

    public int getStart() {
        return base.getStart();
    }

    public String getRemaining() {
        return base.getRemaining();
    }

    public String getRemainingLowerCase() {
        return base.getRemainingLowerCase();
    }

    public SuggestionsBuilderHelper suggest(String suggestion) {
        base.suggest(suggestion);
        return this;
    }

    public SuggestionsBuilderHelper suggest(int value) {
        base.suggest(String.valueOf(value));
        return this;
    }

    public SuggestionsBuilderHelper suggestWithTooltip(String suggestion, TextHelper tooltip) {
        base.suggest(suggestion, tooltip.getRaw());
        return this;
    }

    public SuggestionsBuilderHelper suggestWithTooltip(int value, TextHelper tooltip) {
        base.suggest(String.valueOf(value), tooltip.getRaw());
        return this;
    }

    /**
     * @param suggestions the strings to match
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestMatching(String... suggestions) {
        return suggestMatching(Arrays.asList(suggestions));
    }

    /**
     * @param suggestions the strings to match
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestMatching(Collection<String> suggestions) {
        CommandSource.suggestMatching(suggestions, base);
        return this;
    }

    /**
     * @param identifiers the identifiers to match
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestIdentifier(String... identifiers) {
        return suggestIdentifier(Arrays.asList(identifiers));
    }

    /**
     * @param identifiers the identifiers to match
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestIdentifier(Collection<String> identifiers) {
        CommandSource.suggestIdentifiers(identifiers.stream().map(Identifier::of), base);
        return this;
    }

    /**
     * @param positions the positions to suggest
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestBlockPositions(BlockPosHelper... positions) {
        return suggestPositions(Arrays.stream(positions).map(b -> b.getX() + " " + b.getY() + " " + b.getZ()).collect(Collectors.toList()));
    }

    /**
     * @param positions the positions to suggest
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestBlockPositions(Collection<BlockPosHelper> positions) {
        return suggestPositions(positions.stream().map(b -> b.getX() + " " + b.getY() + " " + b.getZ()).collect(Collectors.toList()));
    }

    /**
     * Positions are strings of the form "x y z" where x, y, and z are numbers or the default
     * minecraft selectors "~" and "^" followed by a number.
     *
     * @param positions the positions to suggest
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestPositions(String... positions) {
        return suggestPositions(Arrays.asList(positions));
    }

    /**
     * Positions are strings of the form "x y z" where x, y, and z are numbers or the default
     * minecraft selectors "~" and "^" followed by a number.
     *
     * @param positions the relative positions to suggest
     * @return self for chaining.
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestPositions(Collection<String> positions) {
        CommandSource.suggestPositions(getRemaining(), positions.stream().map(p -> {
            String[] split = p.split(" ");
            return new CommandSource.RelativePosition(split[0], split[1], split[2]);
        }).collect(Collectors.toList()), base, s -> true);
        return this;
    }

}
