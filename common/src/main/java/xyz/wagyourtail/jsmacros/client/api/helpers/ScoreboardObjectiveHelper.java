package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

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
    
    public ScoreboardObjectiveHelper(ScoreboardObjective o) {
        super(o);
    }
    
    /**
     * @return player name to score map
     */
    public Map<String, Integer> getPlayerScores() {
        Map<String, Integer> scores  = new LinkedHashMap<>();
        for (ScoreboardPlayerScore pl : base.getScoreboard().getAllPlayerScores(base)) {
            scores.put(pl.getPlayerName(), pl.getScore());
        }
        return scores;
    }

    /**
     * @since 1.8.0
     * @return
     */
    public Map<Integer, TextHelper> scoreToDisplayName() {
        Map<Integer, TextHelper> scores  = new LinkedHashMap<>();
        for (ScoreboardPlayerScore pl : base.getScoreboard().getAllPlayerScores(base)) {
            Team team = base.getScoreboard().getPlayerTeam(pl.getPlayerName());
            scores.put(pl.getScore(), new TextHelper(Team.decorateName(team, new LiteralText(pl.getPlayerName()))));
        }
        return scores;
    }

    /**
     * @since 1.7.0
     * @return
     */
    public List<String> getKnownPlayers() {
        return ImmutableList.copyOf(base.getScoreboard().getKnownPlayers());
    }

    /**
     * @since 1.8.0
     * @return
     */
    public List<TextHelper> getKnownPlayersDisplayNames() {
        return ImmutableList.copyOf(base.getScoreboard().getKnownPlayers()).stream()
            .map(e -> new TextHelper(Team.decorateName(base.getScoreboard().getPlayerTeam(e), new LiteralText(e))))
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
        return new TextHelper(base.getDisplayName());
    }
}
