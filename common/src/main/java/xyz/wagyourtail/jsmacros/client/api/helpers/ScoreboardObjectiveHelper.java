package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * @since 1.7.0
     * @return
     */
    public List<String> getKnownPlayers() {
        return ImmutableList.copyOf(base.getScoreboard().getKnownPlayers());
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
