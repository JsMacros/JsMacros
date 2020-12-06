package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.3.0
 */
public class TeamHelper {
    private Team t;

    public TeamHelper(Team t) {
        this.t = t;
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String getName() {
        return t.getName();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getDisplayName() {
        return new TextHelper(t.getDisplayName());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public List<String> getPlayerList() {
        return new ArrayList<>(t.getPlayerList());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public int getColor() {
        return t.getColor().getColorIndex();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getPrefix() {
        return new TextHelper(t.getPrefix());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getSuffix() {
        return new TextHelper(t.getSuffix());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String getCollisionRule() {
        return t.getCollisionRule().toString();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public boolean isFriendlyFire() {
        return t.isFriendlyFireAllowed();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public boolean showFriendlyInvisibles() {
        return t.shouldShowFriendlyInvisibles();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String nametagVisibility() {
        return t.getNameTagVisibilityRule().toString();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String deathMessageVisibility() {
        return t.getDeathMessageVisibilityRule().toString();
    }
    
    public Team getRaw() {
        return t;
    }
}
