package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
* @since 1.2.9
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class ScoreboardsHelper extends BaseHelper<Scoreboard> {

    public ScoreboardsHelper(Scoreboard board) {
        super(board);
    }

    /**
     * @param index
     * @since 1.2.9
     * @return
     */
    public ScoreboardObjectiveHelper getObjectiveForTeamColorIndex(int index) {
        ScoreboardObjective obj = null;
        if (index >= 0) obj = base.getObjectiveForSlot(index + 3);
        return obj == null ? null : new ScoreboardObjectiveHelper(obj);
    }

    /**
    * {@code 0} is tab list, {@code 1} or {@code 3 + getPlayerTeamColorIndex()} is sidebar, {@code 2} should be below name.
    * therefore max slot number is 18.
     * @param slot
     * @since 1.2.9
     * @return
     */
    public ScoreboardObjectiveHelper getObjectiveSlot(int slot) {
        ScoreboardObjective obj = null;
        if (slot >= 0) obj = base.getObjectiveForSlot(slot);
        return obj == null ? null : new ScoreboardObjectiveHelper(obj);
    }

    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    public int getPlayerTeamColorIndex(PlayerEntityHelper<PlayerEntity> entity) {
        return getPlayerTeamColorIndex(entity.getRaw());
    }


    /**
     * @since 1.6.5
     * @return team index for client player
     */
    public int getPlayerTeamColorIndex() {
        return getPlayerTeamColorIndex(MinecraftClient.getInstance().player);
    }

    /**
     * @since 1.3.0
     * @return
     */
    public List<TeamHelper> getTeams() {
        return base.getTeams().stream().map(TeamHelper::new).collect(Collectors.toList());
    }

    /**
     * @param p
     * @since 1.3.0
     * @return
     */
    public TeamHelper getPlayerTeam(PlayerEntityHelper<PlayerEntity> p) {
        return new TeamHelper(getPlayerTeam(p.getRaw()));
    }

    /**
     * @since 1.6.5
     * @return team for client player
     */
    public TeamHelper getPlayerTeam() {
        return new TeamHelper(getPlayerTeam(MinecraftClient.getInstance().player));
    }

    /**
     * @param p
     * @since 1.3.0
     * @return
     */
    protected Team getPlayerTeam(PlayerEntity p) {
        return base.getPlayerTeam(p.getGameProfile().getName());
    }

    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    protected int getPlayerTeamColorIndex(PlayerEntity entity) {
        Team t = base.getPlayerTeam(entity.getGameProfile().getName());
        if (t == null) return -1;
        return t.method_12130().getColorIndex();
    }

    /**
     * @since 1.2.9
     * @return the {@link ScoreboardObjectiveHelper} for the currently displayed sidebar scoreboard.
     */
    public ScoreboardObjectiveHelper getCurrentScoreboard() {
        MinecraftClient mc = MinecraftClient.getInstance();
        int color = getPlayerTeamColorIndex(mc.player);
        ScoreboardObjectiveHelper h = getObjectiveForTeamColorIndex(color);
        if (h == null) h = getObjectiveSlot(1);
        return h;
    }

    public String toString() {
        return String.format("Scoreboard:{\"current\":%s}", getCurrentScoreboard().toString());
    }
}
