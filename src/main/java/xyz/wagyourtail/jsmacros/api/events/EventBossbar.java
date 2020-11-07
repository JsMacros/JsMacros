package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.client.gui.hud.ClientBossBar;
import xyz.wagyourtail.jsmacros.api.helpers.BossBarHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

import java.util.UUID;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventBossbar implements IEvent {
    public final BossBarHelper bossBar;
    public final String uuid;
    public final String type;
    
    public EventBossbar(String type, UUID uuid, ClientBossBar bossBar) {
        if (bossBar != null) this.bossBar = new BossBarHelper(bossBar);
        else this.bossBar = null;
        this.uuid = uuid.toString();
        this.type = type;
        
        profile.triggerMacro(this);
    }

    public String toString() {
        return String.format("%s:{\"bossBar\": %s}", this.getEventName(), bossBar != null ? bossBar.toString() : uuid);
    }
}
