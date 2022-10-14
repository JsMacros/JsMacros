package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ChatComponentText;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.1
 */
@SuppressWarnings("unused")
public class BossBarHelper extends BaseHelper<BossStatus> {

    public BossBarHelper() {
        super(null);
    }
    
    /**
     * @since 1.2.1
     * @return boss bar uuid.
     */
    public String getUUID() {
        return null;
    }
    
    /**
     * @since 1.2.1
     * @return percent of boss bar remaining.
     */
    public float getPercent() {
        return BossStatus.percent;
    }
    
    /**
     * @since 1.2.1
     * @return boss bar color.
     */
    public String getColor() {
        String color = null;
        if (BossStatus.darkenSky)
            return "RAINBOW";
        return "NORMAL";
    }
    
    /**
     * @since 1.2.1
     * @return boss bar notch style.
     */
    public String getStyle() {
        return null;
    }
    
    /**
     * @since 1.2.1
     * @return name of boss bar
     */
    public TextHelper getName() {
        return new TextHelper(new ChatComponentText(BossStatus.name));
    }
    
    public String toString() {
        return String.format("BossBar:{\"name:\":\"%s\", \"percent\":%f}", BossStatus.name, BossStatus.percent);
    }
}
