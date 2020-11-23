package xyz.wagyourtail.jsmacros.client.api.events;

import net.minecraft.client.network.PlayerListEntry;
import xyz.wagyourtail.jsmacros.client.api.helpers.PlayerListEntryHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.UUID;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "PlayerLeave", oldName = "PLAYER_LEAVE")
public class EventPlayerLeave implements BaseEvent {
    public final String UUID;
    public final PlayerListEntryHelper player;
    
    public EventPlayerLeave(UUID uuid, PlayerListEntry player) {
        this.UUID = uuid.toString();
        this.player = new PlayerListEntryHelper(player);
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"player\": %s}", this.getEventName(), player.toString());
    }
}
