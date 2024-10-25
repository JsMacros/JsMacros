package xyz.wagyourtail.jsmacros.client.api.helper.world.entity;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.FormattingHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Locale;

/**
 * @author Wagyourtail
 * @since 1.2.1
 */
@SuppressWarnings("unused")
public class BossBarHelper extends BaseHelper<BossBar> {

    public BossBarHelper(BossBar b) {
        super(b);
    }

    /**
     * @return boss bar uuid.
     * @since 1.2.1
     */
    public String getUUID() {
        return base.getUuid().toString();
    }

    /**
     * @return percent of boss bar remaining.
     * @since 1.2.1
     */
    public float getPercent() {
        return base.getPercent();
    }

    /**
     * @return boss bar color.
     * @since 1.2.1
     */
    @DocletReplaceReturn("BossBarColor")
    public String getColor() {
        return base.getColor().getName().toUpperCase(Locale.ROOT);
    }

    /**
     * @return boss bar notch style.
     * @since 1.2.1
     */
    @DocletReplaceReturn("BossBarStyle")
    public String getStyle() {
        return base.getStyle().getName().toUpperCase(Locale.ROOT);
    }

    /**
     * @return the color of this boss bar.
     * @since 1.8.4
     */
    public int getColorValue() {
        Formatting f = base.getColor().getTextFormat();
        return f.getColorValue() == null ? -1 : f.getColorValue();
    }

    /**
     * @return the format of the boss bar's color.
     * @since 1.8.4
     */
    public FormattingHelper getColorFormat() {
        return new FormattingHelper(base.getColor().getTextFormat());
    }

    /**
     * @return name of boss bar
     * @since 1.2.1
     */
    public TextHelper getName() {
        return TextHelper.wrap(base.getName());
    }

    @Override
    public String toString() {
        return String.format("BossBarHelper:{\"name:\": \"%s\", \"percent\": %f}", base.getName().getString(), base.getPercent());
    }

}
