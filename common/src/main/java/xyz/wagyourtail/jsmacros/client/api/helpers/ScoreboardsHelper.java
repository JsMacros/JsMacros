package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
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
        ScoreObjective obj = null;
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
        ScoreObjective obj = null;
        if (slot >= 0) obj = base.getObjectiveForSlot(slot);
        return obj == null ? null : new ScoreboardObjectiveHelper(obj);
    }

    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    public int getPlayerTeamColorIndex(PlayerEntityHelper<EntityPlayer> entity) {
        return getPlayerTeamColorIndex(entity.getRaw());
    }


    /**
     * @since 1.6.5
     * @return team index for client player
     */
    public int getPlayerTeamColorIndex() {
        return getPlayerTeamColorIndex(Minecraft.getInstance().player);
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
    public TeamHelper getPlayerTeam(PlayerEntityHelper<EntityPlayer> p) {
        return new TeamHelper(getPlayerTeam(p.getRaw()));
    }

    /**
     * @since 1.6.5
     * @return team for client player
     */
    public TeamHelper getPlayerTeam() {
        return new TeamHelper(getPlayerTeam(Minecraft.getInstance().player));
    }

    /**
     * @param p
     * @since 1.3.0
     * @return
     */
    protected ScorePlayerTeam getPlayerTeam(EntityPlayer p) {
        return base.getPlayerTeam(p.getDisplayNameString());
    }

    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    protected int getPlayerTeamColorIndex(EntityPlayer entity) {
        ScorePlayerTeam t = base.getPlayerTeam(entity.getDisplayNameString());
        if (t == null) return -1;
        return t.getFormatting().getColorIndex();
    }

    /**
     * @since 1.2.9
     * @return the {@link ScoreboardObjectiveHelper} for the currently displayed sidebar scoreboard.
     */
    public ScoreboardObjectiveHelper getCurrentScoreboard() {
        Minecraft mc = Minecraft.getInstance();
        int color = getPlayerTeamColorIndex(mc.player);
        ScoreboardObjectiveHelper h = getObjectiveForTeamColorIndex(color);
        if (h == null) h = getObjectiveSlot(1);
        return h;
    }

    public String toString() {
        return String.format("Scoreboard:{\"current\":%s}", getCurrentScoreboard().toString());
    }
}
