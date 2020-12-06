package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;

/**
* @since 1.2.9
 * @author Wagyourtail
 */
public class ScoreboardsHelper {
    private Scoreboard s;
    
    public ScoreboardsHelper(Scoreboard board) {
        s = board;
    }
    
    /**
     * @param index
     * @since 1.2.9
     * @return
     */
    public ScoreboardObjectiveHelper getObjectiveForTeamColorIndex(int index) {
        ScoreboardObjective obj = null;
        if (index >= 0) obj = s.getObjectiveForSlot(index + 3);
        return obj == null ? null : new ScoreboardObjectiveHelper(obj);
    }
    
    /**
    * {@code 0} is tablist, {@code 1} or {@code 3 + getPlayerTeamColorIndex()} is sidebar, {@code 2} should be tab list.
    * therefore max slot number is 18.
     * @param slot
     * @since 1.2.9
     * @return
     */
    public ScoreboardObjectiveHelper getObjectiveSlot(int slot) {
        ScoreboardObjective obj = null;
        if (slot >= 0) obj = s.getObjectiveForSlot(slot);
        return obj == null ? null : new ScoreboardObjectiveHelper(obj);
    }
    
    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    public int getPlayerTeamColorIndex(PlayerEntityHelper entity) {
        return getPlayerTeamColorIndex(entity.getRaw());
    }
    
    protected int getPlayerTeamColorIndex(PlayerEntity entity) {
        Team t = s.getPlayerTeam(entity.getEntityName());
        if (t == null) return -1;
        return t.getColor().getColorIndex();
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
    
    public Scoreboard getRaw() {
        return s;
    }
}
