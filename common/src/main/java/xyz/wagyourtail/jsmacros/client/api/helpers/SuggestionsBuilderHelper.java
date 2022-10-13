package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Locale;

/**
 * @since 1.6.5
 */
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
        return base.getRemaining().toLowerCase(Locale.ROOT);
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
}
