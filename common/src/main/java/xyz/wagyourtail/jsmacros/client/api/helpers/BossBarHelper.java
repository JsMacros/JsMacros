package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.class_2840;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.1
 */
@SuppressWarnings("unused")
public class BossBarHelper extends BaseHelper<class_2840> {

    public BossBarHelper(class_2840 bossBar) {
        super(bossBar);
    }
    
    /**
     * @since 1.2.1
     * @return boss bar uuid.
     */
    public String getUUID() {
        return base.method_12924().toString();
    }
    
    /**
     * @since 1.2.1
     * @return percent of boss bar remaining.
     */
    public float getPercent() {
        return base.method_12926();
    }
    
    /**
     * @since 1.2.1
     * @return boss bar color.
     */
    public String getColor() {
        return base.method_12927().name();
    }
    
    /**
     * @since 1.2.1
     * @return boss bar notch style.
     */
    public String getStyle() {
        return base.method_12928().name();
    }
    
    /**
     * @since 1.2.1
     * @return name of boss bar
     */
    public TextHelper getName() {
        return new TextHelper(base.method_12925());
    }
    
    public String toString() {
        return String.format("BossBar:{\"name:\":\"%s\", \"percent\":%f}", base.method_12925().asString(), base.method_12926());
    }
}
