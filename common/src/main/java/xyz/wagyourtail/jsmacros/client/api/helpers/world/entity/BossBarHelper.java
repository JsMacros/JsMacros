package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Formatting;

import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.FormattingHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
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
     * @since 1.2.1
     * @return boss bar uuid.
     */
    public String getUUID() {
        return base.getUuid().toString();
    }
    
    /**
     * @since 1.2.1
     * @return percent of boss bar remaining.
     */
    public float getPercent() {
        return base.getPercent();
    }
    
    /**
     * @since 1.2.1
     * @return boss bar color.
     */
    @DocletReplaceReturn("BossBarColor")
    public String getColor() {
        return base.getColor().getName().toUpperCase(Locale.ROOT);
    }
    
    /**
     * @since 1.2.1
     * @return boss bar notch style.
     */
    @DocletReplaceReturn("BossBarStyle")
    public String getStyle() {
        return base.getStyle().getName().toUpperCase(Locale.ROOT);
    }

    /**
     * @return the color of this boss bar.
     *
     * @since 1.8.4
     */
    public int getColorValue() {
        Formatting f = base.getColor().getTextFormat();
        return f.getColorValue() == null ? -1 : f.getColorValue();
    }

    /**
     * @return the format of the boss bar's color.
     *
     * @since 1.8.4
     */
    public FormattingHelper getColorFormat() {
        return new FormattingHelper(base.getColor().getTextFormat());
    }
    
    /**
     * @since 1.2.1
     * @return name of boss bar
     */
    public TextHelper getName() {
        return new TextHelper(base.getName());
    }
    
    @Override
    public String toString() {
        return String.format("BossBarHelper:{\"name:\": \"%s\", \"percent\": %f}", base.getName().getString(), base.getPercent());
    }
}