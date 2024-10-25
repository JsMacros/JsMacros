package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.scoreboard.Team;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.FormattingHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
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
     * @return
     * @since 1.3.0
     */
    public String getName() {
        return base.getName();
    }

    /**
     * @return
     * @since 1.3.0
     */
    public TextHelper getDisplayName() {
        return TextHelper.wrap(base.getDisplayName());
    }

    /**
     * @return
     * @since 1.3.0
     */
    public List<String> getPlayerList() {
        return new ArrayList<>(base.getPlayerList());
    }

    /**
     * @return the formatting of this team's color.
     * @since 1.8.4
     */
    public FormattingHelper getColorFormat() {
        return new FormattingHelper(base.getColor());
    }

    /**
     * @return
     * @since 1.3.0
     * @deprecated use {@link #getColorIndex()} instead.
     */
    @Deprecated
    public int getColor() {
        return getColorIndex();
    }

    /**
     * @return the color index of this team.
     * @since 1.8.4
     */
    public int getColorIndex() {
        return base.getColor().getColorIndex();
    }

    /**
     * @return the color value for this team or {@code -1} if it has no color.
     * @since 1.8.4
     */
    public int getColorValue() {
        return base.getColor().getColorValue() == null ? -1 : base.getColor().getColorValue();
    }

    /**
     * @return the name of this team's color.
     * @since 1.8.4
     */
    @DocletReplaceReturn("FormattingColorName")
    public String getColorName() {
        return base.getColor().getName();
    }

    /**
     * @return the scoreboard including this team.
     * @since 1.8.4
     */
    public ScoreboardsHelper getScoreboard() {
        return new ScoreboardsHelper(base.getScoreboard());
    }

    /**
     * @return
     * @since 1.3.0
     */
    public TextHelper getPrefix() {
        return TextHelper.wrap(base.getPrefix());
    }

    /**
     * @return
     * @since 1.3.0
     */
    public TextHelper getSuffix() {
        return TextHelper.wrap(base.getSuffix());
    }

    /**
     * @return
     * @since 1.3.0
     */
    @DocletReplaceReturn("TeamCollisionRule")
    public String getCollisionRule() {
        return base.getCollisionRule().name;
    }

    /**
     * @return
     * @since 1.3.0
     */
    public boolean isFriendlyFire() {
        return base.isFriendlyFireAllowed();
    }

    /**
     * @return
     * @since 1.3.0
     */
    public boolean showFriendlyInvisibles() {
        return base.shouldShowFriendlyInvisibles();
    }

    /**
     * @return
     * @since 1.3.0
     */
    @DocletReplaceReturn("TeamVisibilityRule")
    public String nametagVisibility() {
        return base.getNameTagVisibilityRule().name;
    }

    /**
     * @return
     * @since 1.3.0
     */
    @DocletReplaceReturn("TeamVisibilityRule")
    public String deathMessageVisibility() {
        return base.getDeathMessageVisibilityRule().name;
    }

    @Override
    public String toString() {
        return String.format("TeamHelper:{\"name\": \"%s\"}", getDisplayName().toString());
    }

}
