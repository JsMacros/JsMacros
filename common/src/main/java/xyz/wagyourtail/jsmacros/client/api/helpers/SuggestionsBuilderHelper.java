package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;

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
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestMatching(String... suggestions) {
        CommandSource.suggestMatching(Arrays.asList(suggestions), base);
        return this;
    }

    /**
     * @param identifiers the identifiers to match
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestIdentifier(String... identifiers) {
        CommandSource.suggestIdentifiers(Arrays.stream(identifiers).map(Identifier::new), base);
        return this;
    }

    /**
     * @param positions the positions to suggest
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public SuggestionsBuilderHelper suggestPositions(BlockPosHelper... positions) {
        CommandSource.suggestPositions(getRemaining(), Arrays.stream(positions).map(p -> new CommandSource.RelativePosition(String.valueOf(p.getX()), String.valueOf(p.getY()), String.valueOf(p.getZ()))).toList(), base, s -> true);
        return this;
    }
    
}
