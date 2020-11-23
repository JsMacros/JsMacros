package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.boss.BossBar;

/**
 * @author Wagyourtail
 * @since 1.2.1
 */
public class BossBarHelper {
    private BossBar b;
    public BossBarHelper(BossBar b) {
        this.b = b;
    }
    
    /**
     * @since 1.2.1
     * @return boss bar uuid.
     */
    public String getUUID() {
        return b.getUuid().toString();
    }
    
    /**
     * @since 1.2.1
     * @return percent of boss bar remaining.
     */
    public float getPercent() {
        return b.getPercent();
    }
    
    /**
     * @since 1.2.1
     * @return boss bar color.
     */
    public String getColor() {
        String color = null;
        switch (b.getColor()) {
            case BLUE:
                color = "BLUE";
                break;
            case GREEN:
                color = "GREEN";
                break;
            case PINK:
                color = "PINK";
                break;
            case PURPLE:
                color = "PURPLE";
                break;
            case RED:
                color = "RED";
                break;
            case WHITE:
                color = "WHITE";
                break;
            case YELLOW:
                color = "YELLOW";
                break;
            default:
                break;
        }
        return color;
    }
    
    /**
     * @since 1.2.1
     * @return boss bar notch style.
     */
    public String getStyle() {
        String style = null;
        switch (b.getOverlay()) {
        case NOTCHED_10:
            style = "NOTCHED_10";
            break;
        case NOTCHED_12:
            style = "NOTCHED_12";
            break;
        case NOTCHED_20:
            style = "NOTCHED_20";
            break;
        case NOTCHED_6:
            style = "NOTCHED_6";
            break;
        case PROGRESS:
            style = "PROGRESS";
            break;
        default:
            break;
        }
        return style;
    }
    
    /**
     * @since 1.2.1
     * @return name of boss bar
     */
    public TextHelper getName() {
        return new TextHelper(b.getName());
    }
    
    public BossBar getRaw() {
        return b;
    }
    
    public String toString() {
        return String.format("BossBar:{\"name:\":\"%s\", \"percent\":%f}", b.getName().getString(), b.getPercent());
    }
}
