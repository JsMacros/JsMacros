package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

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
     * @return the formatting for the client player's team, {@code null} if the player is not in a
     *         team.
     *
     * @since 1.8.4
     */
    public FormattingHelper getTeamColorFormatting() {
        Formatting team = getPlayerTeamColor(MinecraftClient.getInstance().player);
        return team == null ? null : new FormattingHelper(team);
    }

    /**
     * @param player the player to get the team color's formatting for.
     * @return the formatting for the client player's team, {@code null} if the player is not in a
     *         team.
     *
     * @since 1.8.4
     */
    public FormattingHelper getTeamColorFormatting(PlayerEntityHelper<PlayerEntity> player) {
        Formatting team = getPlayerTeamColor(player.getRaw());
        return team == null ? null : new FormattingHelper(team);
    }

    /**
     * @param player the player to get the team color for
     * @return the color of the speicifed player's team or {@code -1} if the player is not in a team.
     *
     * @since 1.8.4
     */
    public int getTeamColor(PlayerEntityHelper<PlayerEntity> player) {
        Formatting team = getPlayerTeamColor(player.getRaw());
        return team == null || team.getColorValue() == null ? -1 : team.getColorValue();
    }

    /**
     * @return the color of this player's team or {@code -1} if this player is not in a team.
     *
     * @since 1.8.4
     */
    public int getTeamColor() {
        Formatting team = getPlayerTeamColor(MinecraftClient.getInstance().player);
        return team == null || team.getColorValue() == null ? -1 : team.getColorValue();
    }

    /**
     * @param player the player to get the team color's name for
     * @return the name of the speicifed player's team color or {@code null} if the player is not in
     *         a team.
     *
     * @since 1.8.4
     */
    public String getTeamColorName(PlayerEntityHelper<PlayerEntity> player) {
        Formatting team = getPlayerTeamColor(player.getRaw());
        return team == null ? null : team.getName();
    }

    /**
     * @return the color of this player's team or {@code null} if this player is not in a team.
     *
     * @since 1.8.4
     */
    public String getTeamColorName() {
        Formatting team = getPlayerTeamColor(MinecraftClient.getInstance().player);
        return team == null ? null : team.getName();
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
        return base.getPlayerTeam(p.getEntityName());
    }
    
    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    protected int getPlayerTeamColorIndex(PlayerEntity entity) {
        Formatting color = getPlayerTeamColor(entity);
        return color == null ? -1 : color.getColorIndex();
    }

    /**
     * @param player the player to get the team color for
     * @return the team color for the player or {@code null} if the player is not in a team.
     *
     * @since 1.8.4
     */
    protected Formatting getPlayerTeamColor(PlayerEntity player) {
        Team t = base.getPlayerTeam(player.getEntityName());
        if (t == null) {
            return null;
        }
        return t.getColor();
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

    @Override
    public String toString() {
        return String.format("ScoreboardsHelper:{\"current\": %s}", getCurrentScoreboard().toString());
    }
}