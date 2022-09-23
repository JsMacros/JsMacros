package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.scoreboard.Team;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class TeamHelper extends BaseHelper<Team> {
    public TeamHelper(Team t) {
        super(t);
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String getName() {
        return base.getName();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getDisplayName() {
        return new TextHelper(base.getDisplayName());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public List<String> getPlayerList() {
        return new ArrayList<>(base.getPlayerList());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public int getColor() {
        return base.getColor().getColorIndex();
    }

    /**
     * @return the color value for this team or {@code -1} if it has no color.
     *
     * @since 1.8.4
     */
    public int getColorValue() {
        return base.getColor().getColorValue() == null ? -1 : base.getColor().getColorValue();
    }

    /**
     * @return the name of this team's color.
     *
     * @since 1.8.4
     */
    public String getColorName() {
        return base.getColor().getName();
    }

    /**
     * @return the scoreboard including this team.
     *
     * @since 1.8.4
     */
    public ScoreboardsHelper getScoreboard() {
        return new ScoreboardsHelper(base.getScoreboard());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getPrefix() {
        return new TextHelper(base.getPrefix());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getSuffix() {
        return new TextHelper(base.getSuffix());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String getCollisionRule() {
        return base.getCollisionRule().toString();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public boolean isFriendlyFire() {
        return base.isFriendlyFireAllowed();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public boolean showFriendlyInvisibles() {
        return base.shouldShowFriendlyInvisibles();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String nametagVisibility() {
        return base.getNameTagVisibilityRule().toString();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String deathMessageVisibility() {
        return base.getDeathMessageVisibilityRule().toString();
    }
    
    @Override
    public String toString() {
        return String.format("TeamHelper:{\"name\": \"%s\"}", getDisplayName().toString());
    }
}
