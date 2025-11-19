package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.2.9
 */
@SuppressWarnings("unused")
public class ScoreboardObjectiveHelper extends BaseHelper<ScoreboardObjective> {
    private static final Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR = Comparator.comparing(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);

    public ScoreboardObjectiveHelper(ScoreboardObjective o) {
        super(o);
    }

    /**
     * @return player name to score map
     */
    public Map<String, Integer> getPlayerScores() {
        Map<String, Integer> scores = new LinkedHashMap<>();
        for (ScoreboardEntry pl : base.getScoreboard().getScoreboardEntries(base)) {
            scores.put(pl.owner(), pl.value());
        }
        return scores;
    }

    /**
     * @return
     * @since 1.8.0
     */
    public Map<Integer, TextHelper> scoreToDisplayName() {
        Map<Integer, TextHelper> scores = new LinkedHashMap<>();
        for (ScoreboardEntry pl : base.getScoreboard().getScoreboardEntries(base)) {
            Team team = base.getScoreboard().getScoreHolderTeam(pl.owner());
            scores.put(pl.value(), TextHelper.wrap(Team.decorateName(team, pl.name())));
        }
        return scores;
    }

    /**
     * @since 2.0.0
     */
    public List<TextHelper> getTexts() {
        return base.getScoreboard().getScoreboardEntries(base).stream()
                .filter(ent -> !ent.hidden())
                .sorted(SCOREBOARD_ENTRY_COMPARATOR)
                .limit(15L)
                .map(ent -> {
                    Team team = base.getScoreboard().getScoreHolderTeam(ent.owner());
                    return TextHelper.wrap(Team.decorateName(team, ent.name()));
                })
                .toList();
    }

    /**
     * @return
     * @since 1.7.0
     */
    public List<String> getKnownPlayers() {
        return base.getScoreboard().getKnownScoreHolders().stream().map(ScoreHolder::getNameForScoreboard).toList();
    }

    /**
     * @return
     * @since 1.8.0
     */
    public List<TextHelper> getKnownPlayersDisplayNames() {
        return ImmutableList.copyOf(base.getScoreboard().getKnownScoreHolders()).stream()
                .map(e -> e.getDisplayName() != null ? TextHelper.wrap(e.getDisplayName()) : TextHelper.wrap(Text.literal(e.getNameForScoreboard())))
                .collect(Collectors.toList());
    }

    /**
     * @return name of scoreboard
     * @since 1.2.9
     */
    public String getName() {
        return base.getName();
    }

    /**
     * @return name of scoreboard
     * @since 1.2.9
     */
    public TextHelper getDisplayName() {
        return TextHelper.wrap(base.getDisplayName());
    }

    @Override
    public String toString() {
        return String.format("ScoreboardObjectiveHelper:{\"name\": \"%s\", \"displayName\": \"%s\"}", getName(), getDisplayName());
    }

}
