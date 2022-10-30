package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
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
        return new TextHelper(new LiteralText(base.getName()));
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
        return base.method_12130().getColorIndex();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getPrefix() {
        return new TextHelper(new LiteralText(base.getPrefix()));
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getSuffix() {
        return new TextHelper(new LiteralText(base.getSuffix()));
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
        return base.method_12129().toString();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String deathMessageVisibility() {
        return base.getDeathMessageVisibilityRule().toString();
    }
    
    public String toString() {
        return String.format("Team:{\"name\":\"%s\"}", getDisplayName().toString());
    }
}
